package org.zero.aienglish.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.zero.aienglish.grpc_ontroller.TaskController;

import java.io.IOException;

//@Component
@RequiredArgsConstructor
public class GrpcConfig {
    private final TaskController taskController;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090)
                .addService(taskController)
                .build();
        server.start();
        server.awaitTermination();
    }
}
