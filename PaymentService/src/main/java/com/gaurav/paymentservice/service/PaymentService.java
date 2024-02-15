package com.gaurav.paymentservice.service;

import com.gaurav.paymentservice.model.PaymentRequest;
import com.gaurav.paymentservice.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
