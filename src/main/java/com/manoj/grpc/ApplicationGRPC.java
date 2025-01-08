package com.manoj.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationGRPC {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationGRPC.class);
    private static final int PORT = 50051;
    private Server server;

    public void start() throws Exception {
        server = ServerBuilder.forPort(PORT)
                .addService(new GreeterServiceImpl())
                .build()
                .start();

        logger.info("Server started on port {}", PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server");
            try {
                ApplicationGRPC.this.stop();
            } catch (InterruptedException e) {
                logger.error("Error during shutdown", e);
            }
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationGRPC server = new ApplicationGRPC();
        server.start();
        server.blockUntilShutdown();
    }
}