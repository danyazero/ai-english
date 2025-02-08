package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.zero.aienglish.exception.PaymentGenerationException;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignPayment implements Function<Map<String, String>, LinkedMultiValueMap<String, String>> {
    private final SHAEncoder shaEncoder;
    private final Serializer serializer;

    @Value("${app.liqpay.privateKey}")
    private String privateKey;

    @Override
    public LinkedMultiValueMap<String, String> apply(Map<String, String> data) {
        var serializedData = serializer.apply(data);
        var encodedData = Base64.getEncoder().encodeToString(serializedData.getBytes());
        var paymentSignature = sign(encodedData);

        if (paymentSignature.isEmpty()) {
            log.warn("Encoding signature error.");
            throw new PaymentGenerationException("Encoding signature error.");
        }

        var paymentBody = new LinkedMultiValueMap<String, String>();
        paymentBody.add("data", encodedData);
        paymentBody.add("signature", paymentSignature.get());

        return paymentBody;
    }

    public Optional<String> sign(String encodedData) {
        return shaEncoder.apply(privateKey + encodedData + privateKey, SHAEncoder.Encryption.SHA1);
    }
}
