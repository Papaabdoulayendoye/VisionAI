from concurrent import futures

import grpc

import model
import visionai_pb2
import visionai_pb2_grpc


class VisionModelServicer(visionai_pb2_grpc.VisionModelServicer):
    def PredictBatch(self, request, context):  # noqa: N802 (gRPC naming)
        predictions = []
        for img in request.images:
            result = model.predict(img.id, img.image_base64)
            prediction = visionai_pb2.ImagePrediction(
                id=result["id"],
                label=result["label"],
                score=result["score"],
                features=result["features"],
            )
            predictions.append(prediction)
        return visionai_pb2.ImageBatchResponse(predictions=predictions)


def serve(port: int = 50051) -> None:
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    visionai_pb2_grpc.add_VisionModelServicer_to_server(VisionModelServicer(), server)
    server.add_insecure_port(f"[::]:{port}")
    server.start()
    print(f"VisionAI gRPC server started on port {port}")
    server.wait_for_termination()


if __name__ == "__main__":
    serve()
