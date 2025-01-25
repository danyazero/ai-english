package org.zero.aienglish.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.zero.aienglish.lib.grpc.Task;
import org.zero.aienglish.lib.grpc.TaskServiceGrpc;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.model.TaskType;
import org.zero.aienglish.model.WordResponseDTO;
import org.zero.aienglish.service.TaskService;

import java.util.function.Function;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TaskController extends TaskServiceGrpc.TaskServiceImplBase {
    private final TaskService taskService;

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

    @Override
    public void getTask(Task.TaskRequest request, StreamObserver<Task.TaskResponse> responseObserver) {
        log.info("GRPC called getTask method");
        var userId = request.getUserId();
        Integer themeId = request.getThemeId();

        if (themeId == 0) {
            themeId = null;
        }
        log.info("userId: {}, themeId: {}", userId, themeId);
        var task = taskService.getTask(userId, themeId);

        var words = task.answers().stream()
                .map(mapToGrpcWord())
                .toList();

        var response = Task.TaskResponse.newBuilder()
                .setTaskType(task.taskType().name())
                .setSentenceId(task.sentenceId())
                .setCaption(task.caption())
                .setPattern(task.pattern())
                .setSteps(task.steps())
                .setTheme(task.theme())
                .setTitle(task.title())
                .addAllAnswers(words)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private static Function<WordResponseDTO, Task.Word> mapToGrpcWord() {
        return element -> Task.Word.newBuilder()
                .setWord(element.getWord())
                .setTranslate(element.getTranslate())
                .setOrder(element.getOrder())
                .build();
    }

    @Override
    public void checkTask(Task.TaskCheckRequest request, StreamObserver<Task.TaskCheckResponse> responseObserver) {
        var userId = request.getUserId();
        var taskResult = TaskResultDTO.builder()
                .taskId(request.getAnswer().getTaskId())
                .taskType(TaskType.valueOf(request.getAnswer().getTaskType()))
                .respondTime(request.getAnswer().getRespondTime())
                .answer(request.getAnswer().getAnswer())
                .build();

        var result = taskService.checkTask(userId, taskResult);

        var checkResponse = Task.TaskCheckResponse.newBuilder()
                .setAccepted(result.accepted())
                .setCorrectAnswer(result.correctAnswer())
                .setUserAnswer(result.userAnswer())
                .setMark(result.mark())
                .build();

        responseObserver.onNext(checkResponse);
        responseObserver.onCompleted();
    }
}
