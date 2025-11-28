# VisionAI

VisionAI est un pipeline de vision par ordinateur full-stack construit en **Java (Spring Boot)** et **Python (FastAPI + OpenCV)**.

- Backend Java Spring Boot exposant une **API REST** pour la classification d'images (single + batch).
- Microservice Python léger qui fait **extraction de features** avec OpenCV + **classification heuristique**.
- Intégration Java ↔ Python via **REST** ou **gRPC** (mode configurable).
- **Traitement par batch** et **cache en mémoire** côté Java pour optimiser les appels au modèle.
