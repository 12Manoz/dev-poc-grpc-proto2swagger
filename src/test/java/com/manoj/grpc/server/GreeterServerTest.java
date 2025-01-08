package com.manoj.grpc.server;

import com.manoj.grpc.Greeting.HelloRequest;  // Corrected import
import com.manoj.grpc.Greeting.HelloReply;   // Corrected import
import com.manoj.grpc.GreeterGrpc;
import com.manoj.grpc.GreeterServiceImpl;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GreeterServerTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private GreeterGrpc.GreeterBlockingStub blockingStub;

    @BeforeEach
    public void setUp() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(new GreeterServiceImpl())
                .build()
                .start());

        // Create a client channel and register for automatic graceful shutdown.
        blockingStub = GreeterGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder
                        .forName(serverName)
                        .directExecutor()
                        .build()));
    }

    @Test
    public void testSayHello() {
        // Build request
        HelloRequest request = HelloRequest.newBuilder()
                .setName("Test User")
                .build();

        // Call service and get response
        HelloReply response = blockingStub.sayHello(request);

        // Verify response
        assertNotNull(response);
        assertEquals("Hello, Test User", response.getMessage());
    }

    @Test
    public void testSayHelloEmptyName() {
        // Test with empty name
        HelloRequest request = HelloRequest.newBuilder()
                .setName("")
                .build();

        HelloReply response = blockingStub.sayHello(request);

        assertNotNull(response);
        assertEquals("Hello, ", response.getMessage());
    }
}
