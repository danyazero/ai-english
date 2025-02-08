package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.aienglish.model.PaymentSignature;
import org.zero.aienglish.service.PaymentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void updatePaymentStatus(PaymentSignature paymentSignature) {
        log.info("Update payment body -> {}", paymentSignature);
        paymentService.updatePaymentStatus(paymentSignature);
    }
}
