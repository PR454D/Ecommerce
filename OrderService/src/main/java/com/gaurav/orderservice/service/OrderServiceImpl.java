package com.gaurav.orderservice.service;

import com.gaurav.orderservice.entity.Order;
import com.gaurav.orderservice.exception.CustomException;
import com.gaurav.orderservice.external.client.PaymentService;
import com.gaurav.orderservice.external.client.ProductService;
import com.gaurav.orderservice.external.request.PaymentRequest;
import com.gaurav.orderservice.external.response.PaymentResponse;
import com.gaurav.orderservice.external.response.ProductResponse;
import com.gaurav.orderservice.model.OrderRequest;
import com.gaurav.orderservice.model.OrderResponse;
import com.gaurav.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ProductService productService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    RestTemplate restTemplate;


    @Override
    public long placeOrder(OrderRequest orderRequest) {

        //order entity -> save the data with status Order Created.
        //Product Service - Block Products (Reduce the Quantity)
        //Payment Service -> Payments -> Success -> COMPLETED,Else
        //CANCELLED

        log.info("Placing Order Request: {}",orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());
        log.info("Creating Order With Status CREATED");

        Order order= Order.builder().amount(orderRequest.getTotalAmount()).orderStatus("CREATED").productId(orderRequest.getProductId()).orderDate(Instant.now()).quantity(orderRequest.getQuantity()).build();
        order=orderRepository.save(order);

        log.info("Calling Payment Service to complete the payment");

        PaymentRequest paymentRequest=PaymentRequest.builder().orderId(order.getId()).paymentMode(orderRequest.getPaymentMode()).amount(orderRequest.getTotalAmount()).build();

        String orderStatus=null;

        try{
            paymentService.doPayment(paymentRequest);
            log.info("Payment Done Successfully. Changing the orderStatus to PLACED");
            orderStatus="PLACED";

        }catch (Exception e){
            log.error("Error Occurred in payment. Changing order to PAYMENT_FAILED");
            orderStatus="PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order Placed Successfully with Order Id: {}",orderRequest);
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get Order Details for OrderId : {}",orderId);

        Order order =orderRepository.findById(orderId).orElseThrow(()-> new CustomException("Order not found for the orderId ","NOT_FOUND",404));
        log.info("Invoking Product Service to fetch the product for id : {}",order.getProductId());
        ProductResponse productResponse=restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(), ProductResponse.class);

        log.info("Invoking Payment Service to fetch the payment Details for id : {}",order.getProductId());
        PaymentResponse paymentResponse=restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getId(), PaymentResponse.class);

        OrderResponse.ProductDetails productDetails=OrderResponse.ProductDetails.builder().productName(productResponse.getProductName()).productId(productResponse.getProductId()).build();

        OrderResponse.PaymentDetails paymentDetails=OrderResponse.PaymentDetails.builder().paymentId(paymentResponse.getPaymentId()).paymentStatus(paymentResponse.getStatus()).paymentDate(paymentResponse.getPaymentDate()).paymentMode(paymentResponse.getPaymentMode()).build();
        return OrderResponse.builder().orderId(order.getId()).orderStatus(order.getOrderStatus()).amount(order.getAmount()).orderDate(order.getOrderDate()).productDetails(productDetails).paymentDetails(paymentDetails).build();
    }
}
