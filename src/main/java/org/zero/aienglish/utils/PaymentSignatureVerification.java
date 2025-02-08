package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.model.PaymentSignature;

import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSignatureVerification implements Function<PaymentSignature, Boolean> {
    private final SignPayment signPayment;

    @Override
    public Boolean apply(PaymentSignature body) {
        var paymentSignature = signPayment.sign(body.data());

        if (paymentSignature.isEmpty()) {
            log.warn("Error signing payment update data");
            return false;
        }

        return paymentSignature.get().equals(body.signature());
    }

}
