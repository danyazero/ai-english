package org.zero.aienglish.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentCallback(
        String amount,
        @JsonProperty("order_id") String orderId,
        @JsonProperty("liqpay_order_id") String transactionId,
        String status
) {
}
