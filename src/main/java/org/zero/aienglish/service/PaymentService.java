package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.zero.aienglish.entity.Checkout;
import org.zero.aienglish.entity.Subscription;
import org.zero.aienglish.exception.PaymentGenerationException;
import org.zero.aienglish.mapper.SubscriptionMapper;
import org.zero.aienglish.model.CheckoutDTO;
import org.zero.aienglish.model.PaymentCallback;
import org.zero.aienglish.model.PaymentSignature;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.Base64Decoder;
import org.zero.aienglish.utils.Deserializer;
import org.zero.aienglish.utils.PaymentSignatureVerification;
import org.zero.aienglish.utils.SignPayment;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentService {
    private final SignPayment signPayment;
    private final Base64Decoder base64Decoder;
    private final UserRepository userRepository;
    private final CheckoutRepository checkoutRepository;
    private final SubscriptonRepository subscriptonRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentSignatureVerification paymentSignatureVerification;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${app.liqpay.serverUrl}")
    private String serverLink;

    @Value("${bot.name}")
    private String botName;

    @Value("${app.url}")
    private String appURL;

    @Value("${app.liqpay.publicKey}")
    private String publicKey;

    @Value("${app.liqpay.apiVersion}")
    private String apiVersion;

    @Transactional
    public CheckoutDTO getPaymentCheckout(Integer userId, Integer planId) {

        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("User not found");
            throw new PaymentGenerationException("Такого користувача не існує.");
        }

        var foundedPlan = subscriptionPlanRepository.findById(planId);
        if (foundedPlan.isEmpty()) {
            log.warn("Selected plan not found.");
            throw new PaymentGenerationException("Обраний план підписки не існує.");
        }

        var generateCheckout = Checkout.builder()
                .subscriptionPlan(foundedPlan.get())
                .user(user.get())
                .build();
        var savedCheckout = checkoutRepository.save(generateCheckout);

        HashMap<String, String> request = new HashMap<>();

        request.put("action", "pay");
        request.put("version", apiVersion);
        request.put("public_key", publicKey);
        request.put("currency", "UAH");
        request.put("order_id", savedCheckout.getId().toString());
        request.put("amount", String.valueOf(foundedPlan.get().getPrice()));
        request.put("description", "Оплата підписки на телеграм бота - " + botName + ". (" + foundedPlan.get().getDurationDays() + " дн.)");


        request.put("server_url", appURL);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<LinkedMultiValueMap<String, String>> entity = new HttpEntity<>(signPayment.apply(request), headers);
        var response = new RestTemplate().postForEntity(serverLink, entity, String.class);

        var locationValue = response.getHeaders().get(HttpHeaders.LOCATION);

        if (locationValue == null) {
            log.warn("No location header found");
            throw new PaymentGenerationException("Помилка генерації сторінки оплати.");
        }

        return CheckoutDTO.builder()
                .subscriptionPlan(SubscriptionMapper.map(foundedPlan.get()))
                .checkoutLink(locationValue.getFirst())
                .build();
    }

    @Transactional
    public void updatePaymentStatus(PaymentSignature paymentSignature) {
        if (!paymentSignatureVerification.apply(paymentSignature)) {
            log.warn("Invalid payment signature.");
            return;
        }

        var decodedData = base64Decoder.apply(paymentSignature.data());
        if (decodedData.isEmpty()) {
            log.warn("Error decoding payment status data from base64");
            return;
        }

        var deserializedData = Deserializer.apply(decodedData.get(), PaymentCallback.class);
        log.info("Subscription update data -> {}", deserializedData);

        if (deserializedData.status().equals("success")) {
            log.info("Successful payment with id -> {}", deserializedData.orderId());
            UUID transactionCheckoutId = UUID.fromString(deserializedData.orderId());
            var checkout = checkoutRepository.findById(transactionCheckoutId);
            if (checkout.isEmpty()) {
                log.warn("Error update subscription status, subscription not found");
                throw new PaymentGenerationException("Checkout not found");
            }

            var startDate = Instant.now();

            var actualSubscription = subscriptionRepository.findFirstByUserId(checkout.get().getUser().getId());
            if (actualSubscription.isPresent()) {
                startDate = actualSubscription.get().getValidDue();
            }

            var validDueTo = startDate.plus(checkout.get().getSubscriptionPlan().getDurationDays(), ChronoUnit.DAYS);

            var subscription = Subscription.builder()
                    .at(Instant.now())
                    .user(checkout.get().getUser())
                    .validDue(validDueTo)
                    .checkout(checkout.get())
                    .subscriptionPlan(checkout.get().getSubscriptionPlan())
                    .build();
            subscriptonRepository.save(subscription);

            actualSubscription.ifPresent(subscriptonRepository::delete);

            log.info("Subscription successful. Valid due to -> {}", validDueTo);
        }
    }
}
