package com.manoj.grpc.client;

import com.manoj.grpc.Greeting.HelloRequest;  // Corrected import
import com.manoj.grpc.Greeting.HelloReply;   // Corrected import
import com.manoj.grpc.GreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GreeterClientTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private GreeterGrpc.GreeterStub asyncStub;
    private ManagedChannel channel;

    @BeforeEach
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(new MockGreeterService())
                .build()
                .start());

        // Create a client channel
        channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());
        asyncStub = GreeterGrpc.newStub(channel);
    }

    @Test
    public void testAsyncHello() throws Exception {
        HelloRequest request = HelloRequest.newBuilder()
                .setName("Test Client")
                .build();

        final CountDownLatch latch = new CountDownLatch(1);
        final HelloReply[] response = new HelloReply[1];

        asyncStub.sayHello(request, new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply reply) {
                response[0] = reply;
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals("Mock Hello, Test Client", response[0].getMessage());
    }

    // Mock service for testing
    private static class MockGreeterService extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Mock Hello, " + request.getName())
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
