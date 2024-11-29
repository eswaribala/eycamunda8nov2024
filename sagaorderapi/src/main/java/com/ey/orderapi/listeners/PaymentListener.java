package com.ey.orderapi.listeners;

import com.ey.orderapi.dtos.OrderRequest;
import com.ey.orderapi.dtos.PaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@KafkaListener(topics = "eypaymentnov2024", groupId = "saga-ecommerce-group")
public class PaymentListener {

    @Autowired
    private KafkaTemplate<Object,Object> kafkaTemplate;

    @Autowired
    private ZeebeClient zeebeClient;

    public static PaymentRequest paymentInfo;
    @KafkaHandler(isDefault = true)
    public void receivePaymentEvent(String message) throws JsonProcessingException {

        System.out.println(message);

        ObjectMapper objectMapper=new ObjectMapper();
        // Register the module to handle LocalDate
        objectMapper.registerModule(new JavaTimeModule());

        // Deserialize variables to a Map
        paymentInfo = objectMapper.readValue(message, PaymentRequest.class);
        System.out.println(paymentInfo);
        Map<String,PaymentRequest> paymentRequestMap=new HashMap<>();
        paymentRequestMap.put("PaymentInfo",paymentInfo);
        if(paymentInfo.getAmount()>500) {
            zeebeClient.newPublishMessageCommand()
                    .messageName("Message_Success")
                    .correlationKey("3001")
                    .variables(paymentRequestMap)
                    .timeToLive(Duration.ofMinutes(3))
                    .send()
                    .exceptionally((throwable) -> {
                        throw new RuntimeException("Job not found");
                    });
        }else{
            zeebeClient.newPublishMessageCommand()
                    .messageName("Message_Failure")
                    .correlationKey("3002")
                    .variables(paymentRequestMap)
                    .timeToLive(Duration.ofMinutes(3))
                    .send()
                    .exceptionally((throwable) -> {
                        throw new RuntimeException("Job not found");
                    });
        }

    }
}
