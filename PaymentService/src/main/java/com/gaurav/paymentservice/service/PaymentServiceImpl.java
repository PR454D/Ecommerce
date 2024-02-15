package com.gaurav.paymentservice.service;

import com.gaurav.paymentservice.entity.TransactionDetails;
import com.gaurav.paymentservice.model.PaymentMode;
import com.gaurav.paymentservice.model.PaymentRequest;
import com.gaurav.paymentservice.model.PaymentResponse;
import com.gaurav.paymentservice.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    TransactionDetailsRepository transactionDetailsRepository;
    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording Payment Details: {}",paymentRequest);
        TransactionDetails transactionDetails= TransactionDetails.builder().paymentDate(Instant.now()).paymentMode(paymentRequest.getPaymentMode().name()).paymentStatus("SUCCESS").orderId(paymentRequest.getOrderId()).referenceNumber(paymentRequest.getReferenceNumber()).amount(paymentRequest.getAmount()).build();
        transactionDetailsRepository.save(transactionDetails);
        log.info("Transaction Completed with Id: {}",transactionDetails.getId());
        return transactionDetails.getId();

    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(String orderId) {
        log.info("Getting Payment Details for Order Id:{}",orderId);
        TransactionDetails transactionDetails=transactionDetailsRepository.findByOrderId(Long.parseLong(orderId));
        return PaymentResponse.builder().paymentId(transactionDetails.getId()).paymentDate(transactionDetails.getPaymentDate()).paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode())).amount(transactionDetails.getAmount()).OrderId(transactionDetails.getOrderId()).status(transactionDetails.getPaymentStatus()).build();
    }
}
