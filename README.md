# VisionAI

VisionAI est un pipeline de vision par ordinateur full-stack construit en **Java (Spring Boot)** et **Python (FastAPI + OpenCV)**.

- Backend Java Spring Boot exposant une **API REST** pour la classification d'images (single + batch).
- Microservice Python léger qui fait **extraction de features** avec OpenCV + **classification heuristique**.
- Intégration Java ↔ Python via **REST** ou **gRPC** (mode configurable).
- **Traitement par batch** et **cache en mémoire** côté Java pour optimiser les appels au modèle.

## 1. Architecture

### 1.1 Côté Java (Spring Boot)

Packages principaux :

- `org.example.visionai.domain`
  - `ImageRequest` : image à classifier (id, image encodée en Base64).
  - `ImageBatchRequest` : liste d'`ImageRequest`.
  - `ImagePrediction` : résultat de prédiction (id, label, score, features).
  - `ImageBatchResponse` : liste d'`ImagePrediction`.

- `org.example.visionai.application`
  - `PythonModelClient` : interface (port) pour appeler le modèle Python.
  - `VisionPipelineService` :
    - Orchestration du pipeline.
    - Gère un **cache en mémoire** (clé = hash SHA-256 du contenu Base64).
    - Pour un batch :
      - Cherche d'abord dans le cache.
      - N'envoie au modèle Python que les images non en cache.
      - Reconstruit la réponse dans le **même ordre** que la requête.

- `org.example.visionai.infrastructure`
  - `VisionAiConfiguration` : configuration Spring (beans) :
    - Choix du client Python : `stub`, `rest` ou `grpc` via `visionai.python.client-mode`.
    - Configuration de `WebClient` (REST) et du `ManagedChannel` gRPC.

- `org.example.visionai.infrastructure.api`
  - `VisionController` :
    - `POST /api/v1/vision/predict` : prédiction pour une image.
    - `POST /api/v1/vision/predict-batch` : prédiction pour un batch.
  - DTOs : `ImageRequestDto`, `ImageBatchRequestDto`.

- `org.example.visionai.infrastructure.model`
  - `StubPythonModelClient` : client stub qui renvoie des prédictions factices (utile quand Python n'est pas lancé).
  - `RestPythonModelClient` : utilise `WebClient` pour appeler le microservice Python REST.
  - `GrpcPythonModelClient` : utilise les stubs gRPC générés à partir de `visionai.proto`.

### 1.2 Côté Python (`python-ml/`)

Fichiers principaux :

- `model.py` :
  - Décode une image en Base64 → image OpenCV.
  - Extrait des features simples : largeur, hauteur, ratio, moyennes B/G/R.
  - Classifie avec une heuristique :
    - Label `BRIGHT` ou `DARK` selon la luminosité moyenne.
    - Score normalisé entre 0 et 1.

- `schemas.py` : modèles Pydantic (`ImageRequest`, `ImageBatchRequest`, `ImagePrediction`, `ImageBatchResponse`).

- `main.py` : application FastAPI
  - `GET /health` : health-check.
  - `POST /predict` : single image.
  - `POST /predict-batch` : batch.

- `grpc_server.py` : serveur gRPC utilisant le proto partagé `visionai.proto`.

- `requirements.txt` : dépendances Python (FastAPI, Uvicorn, NumPy, OpenCV, gRPC...).


## 2. API REST Java

### 2.1 Endpoints

Base path : `/api/v1/vision`.

- `POST /api/v1/vision/predict`

  Request JSON :

  ```json
  {
    "id": "img-1",
    "imageBase64": "..." // contenu Base64 de l'image
  }
  ```

  Response JSON (exemple) :

  ```json
  {
    "id": "img-1",
    "label": "BRIGHT",
    "score": 0.85,
    "features": {
      "width": 640.0,
      "height": 480.0,
      "aspect_ratio": 1.33,
      "mean_b": 120.0,
      "mean_g": 130.0,
      "mean_r": 140.0
    }
  }
  ```

- `POST /api/v1/vision/predict-batch`

  Request JSON :

  ```json
  {
    "images": [
      { "id": "img-1", "imageBase64": "..." },
      { "id": "img-2", "imageBase64": "..." }
    ]
  }
  ```

  Response JSON :

  ```json
  {
    "predictions": [
      { "id": "img-1", "label": "BRIGHT", "score": 0.8, "features": {"...": 0.0} },
      { "id": "img-2", "label": "DARK",   "score": 0.2, "features": {"...": 0.0} }
    ]
  }
  ```

## 3. Modes d'intégration Java ↔ Python

La classe `VisionAiConfiguration` lit la propriété `visionai.python.client-mode` et choisit l'implémentation de `PythonModelClient`.

### 3.1 Mode stub (par défaut)

Aucune dépendance externe, tout tourne côté Java.

```properties
visionai.python.client-mode=stub
```

Dans ce mode, `StubPythonModelClient` renvoie des prédictions factices, ce qui permet de tester rapidement l'API REST.

### 3.2 Mode REST

Le backend Java appelle le microservice Python FastAPI.

#### Côté Python

```bash
cd python-ml
python -m venv .venv
.venv\\Scripts\\activate  # PowerShell / CMD sur Windows
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8001 --reload
```

#### Côté Java

Configurer :

```properties
visionai.python.client-mode=rest
visionai.python.rest.base-url=http://localhost:8001
```

Puis lancer :

```bash
./mvnw spring-boot:run
```

### 3.3 Mode gRPC

Le backend Java utilise les stubs gRPC générés à partir de `src/main/proto/visionai.proto`.

#### Côté Python

Générer les stubs Python :

```bash
cd python-ml
python -m venv .venv
.venv\\Scripts\\activate
pip install -r requirements.txt
python -m grpc_tools.protoc \
  -I ../src/main/proto \
  --python_out=. \
  --grpc_python_out=. \
  ../src/main/proto/visionai.proto

python grpc_server.py
```

#### Côté Java

Configurer :

```properties
visionai.python.client-mode=grpc
visionai.python.grpc.host=localhost
visionai.python.grpc.port=50051
```

Puis lancer :

```bash
./mvnw spring-boot:run
```

## 4. Cache et traitement par batch

Le cache est géré dans `VisionPipelineService` :

- Clé = hash SHA-256 de la chaîne Base64 de l'image.
- Valeur = `ImagePrediction` retournée par le modèle Python.

### 4.1 Cas single

1. On calcule la clé à partir de `imageBase64`.
2. Si la prédiction est en cache → on la renvoie directement.
3. Sinon → appel au modèle Python (`PythonModelClient.predict`), puis stockage dans le cache.

### 4.2 Cas batch

Pour un `ImageBatchRequest` :

1. On parcourt la liste des images :
   - Si en cache → on remplit directement la prédiction à l’index correspondant.
   - Sinon → on enregistre l’index + l’image dans une map et on met un placeholder.
2. On appelle `PythonModelClient.predictBatch` **uniquement** avec la liste des images non en cache.
3. On remet les prédictions retournées à leur place dans la liste finale, dans l’ordre initial.
4. On enregistre ces prédictions dans le cache.

## 5. Tests

Tests Java :

- `VisionAiApplicationTests` : vérifie que le contexte Spring démarre.
- `VisionPipelineServiceTest` :
  - Vérifie que pour la même image, le service n’appelle le modèle Python qu’une seule fois (grâce au cache).
  - Vérifie que pour un batch répété, seul le premier appel déclenche un accès au modèle.

Vous pouvez lancer les tests avec :

```bash
./mvnw test
```

## 6. Exemples d'utilisation (curl)

Assurez-vous que :
- Le microservice Python REST tourne (`visionai.python.client-mode=rest`), ou que vous utilisez le mode `stub`.
- L'application Spring Boot est démarrée.

### Single prediction

```bash
curl -X POST "http://localhost:8080/api/v1/vision/predict" \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"img-1\",\"imageBase64\":\"dGVzdA==\"}"
```

### Batch prediction

```bash
curl -X POST "http://localhost:8080/api/v1/vision/predict-batch" \
  -H "Content-Type: application/json" \
  -d "{\"images\":[{\"id\":\"img-1\",\"imageBase64\":\"dGVzdA==\"},{\"id\":\"img-2\",\"imageBase64\":\"dGVzdDI=\"}]}"
```

## 7. Résumé

VisionAI démontre :

- Comment combiner **Java Spring Boot** et **Python FastAPI/OpenCV** pour un pipeline de vision.
- Comment exposer un modèle via **REST** et **gRPC**.
- Comment implémenter un **traitement par batch** et un **cache** pour réduire la charge sur le modèle.

Le projet est pensé pour être un bon support pédagogique tout en restant proche de patterns utilisés en production (ports/adapters, séparation domain/application/infrastructure).