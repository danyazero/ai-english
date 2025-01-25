package org.zero.aienglish.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.zero.aienglish.lib.grpc.User;
import org.zero.aienglish.lib.grpc.UserServiceGrpc;
import org.zero.aienglish.model.UserDTO;
import org.zero.aienglish.service.UserService;

@GrpcService
@RequiredArgsConstructor
public class UserController extends UserServiceGrpc.UserServiceImplBase {
    private final UserService userService;

    @Override
    public void createUser(User.CreateUserRequest request, StreamObserver<User.UserResponse> responseObserver) {
        var username = request.getUsername();

        var userDTO = new UserDTO(username);
        var user = userService.createUser(userDTO);

        var userResponse = User.UserResponse.newBuilder()
                .setUserId(user)
                .build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(User.UpdateUserRequest request, StreamObserver<User.UserResponse> responseObserver) {
        var username = request.getUsername();
        var userId = request.getUserId();

        var userDTO = new UserDTO(username);
        var user = userService.updateUser(userId, userDTO);

        var userResponse = User.UserResponse.newBuilder()
                .setUserId(user)
                .build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }
}
