package org.zero.aienglish.grpc_ontroller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.zero.aienglish.lib.grpc.Task;
import org.zero.aienglish.lib.grpc.TaskServiceGrpc;
import org.zero.aienglish.model.TaskAnswer;
import org.zero.aienglish.service.TaskService;

import java.util.function.Function;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TaskController extends TaskServiceGrpc.TaskServiceImplBase {
    private final TaskService taskService;

    @Override
    public void revert(Task.RevertRequest request, StreamObserver<Task.TaskState> responseObserver) {
        var userId = request.getUserId();

        var taskState = taskService.revert(userId);

        if (taskState.isEmpty()) {
            var revertResponse = Task.TaskState.newBuilder()
                    .build();

            responseObserver.onNext(revertResponse);
            responseObserver.onCompleted();
            return;
        }

        var answers = taskState.get().answers().stream()
                .map(mapToGrpcWord())
                .toList();

        var revertResponse = Task.TaskState.newBuilder()
                .setCurrentStep(taskState.get().currentStep())
                .setAmountStep(taskState.get().amountSteps())
                .setCaption(taskState.get().caption())
                .setTitle(taskState.get().title())
                .addAllAnswers(answers)
                .build();

        responseObserver.onNext(revertResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void check(Task.TaskCheckRequest request, StreamObserver<Task.TaskCheckResponse> responseObserver) {
        var userId = request.getUserId();

        var result = taskService.checkTask(userId, request.getAnswer());

        var answers = result.state().answers().stream()
                .map(mapToGrpcWord())
                .toList();


        var state = Task.TaskState.newBuilder()
                .setCurrentStep(result.state().currentStep())
                .setAmountStep(result.state().amountSteps())
                .setCaption(result.state().caption())
                .setTitle(result.state().title())
                .addAllAnswers(answers)
                .build();

        var checkResponse = Task.TaskCheckResponse.newBuilder()
                .setTaskId(result.taskId())
                .setChecked(result.checked())
                .setState(state);


        if (result.checked()) {
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
    public void getTaskExplain(Task.TaskHelpRequest
                                       request, StreamObserver<Task.TaskHelpResponse> responseObserver) {
        var taskId = request.getUserId();
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
