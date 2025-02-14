package org.zero.aienglish.grpc_ontroller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.zero.aienglish.lib.grpc.Subscription;
import org.zero.aienglish.lib.grpc.SubscriptionServiceGrpc.SubscriptionServiceImplBase;
import org.zero.aienglish.mapper.SubscriptionMapper;
import org.zero.aienglish.service.SubscriptionService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class SubscriptionController extends SubscriptionServiceImplBase {
    private final SubscriptionService subscriptionService;

    @Override
    public void userSubscription(Subscription.UserSubscriptionPlanRequest request, StreamObserver<Subscription.UserSubscriptionPlanResponse> responseObserver) {
        var userId = request.getUserId();

        var userSubscriptionDetails = subscriptionService.getUserSubscriptionDetails(userId);

        var userAvailablePlans = userSubscriptionDetails.availablePlans().stream()
                .map(SubscriptionMapper::mapToGrpc)
                .toList();

        var response = Subscription.UserSubscriptionPlanResponse.newBuilder()
                .addAllAvailablePlans(userAvailablePlans);

        if (userSubscriptionDetails.userSubscription().isPresent()) {
            var userSubscription = SubscriptionMapper.mapToGrpc(userSubscriptionDetails.userSubscription().get());
            response.setUserSubscription(userSubscription);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void planCheckout(Subscription.SubscriptionCheckoutRequest request, StreamObserver<Subscription.SubscriptionCheckoutResponse> responseObserver) {
        var userId = request.getUserId();
        var planId = request.getPlanId();

        var subscriptionPlanCheckout = subscriptionService.getSubscriptionPlanCheckout(userId, planId);

        var subscriptionPlan = Subscription.SubscriptionPlan.newBuilder()
                .setDurationDays(subscriptionPlanCheckout.subscriptionPlan().durationDays())
                .setPrice(subscriptionPlanCheckout.subscriptionPlan().price())
                .setName(subscriptionPlanCheckout.subscriptionPlan().name())
                .build();

        var response = Subscription.SubscriptionCheckoutResponse.newBuilder()
                .setCheckoutLink(subscriptionPlanCheckout.checkoutLink())
                .setPlanDetails(subscriptionPlan)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
