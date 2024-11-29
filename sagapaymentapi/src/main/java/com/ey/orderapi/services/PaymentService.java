package com.ey.orderapi.services;

import com.ey.orderapi.dtos.PaymentRequest;
import com.ey.orderapi.listeners.OrderListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {

    @Autowired
    private KafkaTemplate<Object,Object> kafkaTemplate;
    @Value("${paymentTopicName}")
    private String paymentTopicName;
    public CompletableFuture<SendResult<Object,Object>> makePayment() throws JsonProcessingException {
        PaymentRequest paymentRequest=new PaymentRequest();
        paymentRequest.setPaymentId(new Faker().number().numberBetween(1L,50000L));
        paymentRequest.setAmount(OrderListener.orderInfo.getOrderAmount());
        paymentRequest.setPaymentStatus(true);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(paymentRequest);
        return kafkaTemplate.send(paymentTopicName,json);

    }
}
