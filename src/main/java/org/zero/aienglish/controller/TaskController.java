package org.zero.aienglish.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.zero.aienglish.lib.grpc.Task;
import org.zero.aienglish.lib.grpc.TaskServiceGrpc;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.model.TaskAnswer;
import org.zero.aienglish.service.TaskService;

import java.util.List;
import java.util.function.Function;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TaskController extends TaskServiceGrpc.TaskServiceImplBase {
    private final TaskService taskService;

    @Override
    public void revert(Task.revertRequest request, StreamObserver<Task.TaskState> responseObserver) {
        var userId = request.getUserId();

        var taskState = taskService.revert(userId);

        var answers = taskState.answers().stream()
                .map(mapToGrpcWord())
                .toList();

        var revertResponse = Task.TaskState.newBuilder()
                .setTitle(taskState.title())
                .setAmountStep(taskState.amountSteps())
                .setCurrentStep(taskState.currentStep())
                .addAllAnswers(answers)
                .build();

        responseObserver.onNext(revertResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void check(Task.TaskCheckRequest request, StreamObserver<Task.TaskCheckResponse> responseObserver) {
        var userId = request.getUserId();

        var result = taskService.checkTask(userId, request.getAnswer());


        var checkResponse = Task.TaskCheckResponse.newBuilder()
                .setTaskId(result.taskId())
                .setChecked(result.checked());

        if (!result.checked()) {
            List<Task.Word> answers = result.state().answers().stream()
                    .map(mapToGrpcWord())
                    .toList();

            var state = Task.TaskState.newBuilder()
                    .setCurrentStep(result.state().currentStep())
                    .setAmountStep(result.state().amountSteps())
                    .setTitle(result.state().title())
                    .addAllAnswers(answers)
                    .build();

            checkResponse.setState(state);
        } else {
            var checkResult = Task.TaskCheckResult.newBuilder()
                    .setCorrectAnswer(result.result().correctAnswer())
                    .setUserAnswer(result.result().userAnswer())
                    .setAccepted(result.result().accepted())
                    .setMark(result.result().mark())
                    .build();

            checkResponse.setResult(checkResult);
        }

        responseObserver.onNext(checkResponse.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getTaskExplain(Task.TaskHelpRequest request, StreamObserver<Task.TaskHelpResponse> responseObserver) {
        var taskId = request.getTaskId();
        var taskExplain = taskService.getTaskTheoryHelp(taskId);

        var explainResponse = Task.TaskHelpResponse.newBuilder()
                .setText(taskExplain)
                .build();
        responseObserver.onNext(explainResponse);
        responseObserver.onCompleted();
    }

    private static Function<TaskAnswer, Task.Word> mapToGrpcWord() {
        return element -> Task.Word.newBuilder()
                .setWord(element.word())
                .setOrder(element.order())
                .build();
    }
}
