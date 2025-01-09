package com.manoj.grpc;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreeterServiceImpl extends GreeterGrpc.GreeterImplBase {
    private static final Logger logger = LoggerFactory.getLogger(GreeterServiceImpl.class);

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        logger.info("Received hello request from: {}", request.getName());

        try {
            // Create the response
            HelloResponse reply = HelloResponse.newBuilder()
                    .setMessage("Hello, " + request.getName())
                    .build();

            // Send the response
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

            logger.info("Sent response to: {}", request.getName());
        } catch (Exception e) {
            logger.error("Error processing request from: " + request.getName(), e);
            responseObserver.onError(e);
        }
    }
}
