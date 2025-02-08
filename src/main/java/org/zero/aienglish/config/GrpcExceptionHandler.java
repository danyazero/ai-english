package org.zero.aienglish.config;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import org.zero.aienglish.exception.PaymentGenerationException;
import org.zero.aienglish.exception.SubscriptionExpiredException;
import org.zero.aienglish.exception.TaskNotFoundException;

@GrpcAdvice
public class GrpcExceptionHandler {
    @net.devh.boot.grpc.server.advice.GrpcExceptionHandler
    public Status handlePremiumFinishedException(SubscriptionExpiredException exception) {
        return Status.PERMISSION_DENIED.withDescription(exception.getMessage()).withCause(exception);
    }

    @net.devh.boot.grpc.server.advice.GrpcExceptionHandler
    public Status handleCheckoutGenerationException(PaymentGenerationException exception) {
        return Status.CANCELLED.withDescription(exception.getMessage()).withCause(exception);
    }

    @net.devh.boot.grpc.server.advice.GrpcExceptionHandler
    public Status handleTaskNotFoundFinishedException(TaskNotFoundException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage()).withCause(exception);
    }
}
