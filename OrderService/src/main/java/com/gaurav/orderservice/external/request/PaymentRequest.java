package com.gaurav.orderservice.external.request;

import com.gaurav.orderservice.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private long orderId;
    private long amount;
    private String referenceNumber;
    private PaymentMode paymentMode;

}
