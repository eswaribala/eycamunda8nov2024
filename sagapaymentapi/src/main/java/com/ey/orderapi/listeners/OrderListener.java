package com.ey.orderapi.listeners;

import com.ey.orderapi.dtos.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@KafkaListener(topics = "eyordernov2024", groupId = "saga-ecommerce-group")
public class OrderListener {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public static OrderRequest orderInfo;
    @KafkaHandler(isDefault = true)
    public void receiveOrderEvent(String message) throws JsonProcessingException {

        System.out.println(message);

        ObjectMapper objectMapper=new ObjectMapper();
        // Register the module to handle LocalDate
        objectMapper.registerModule(new JavaTimeModule());

        // Deserialize variables to a Map
        orderInfo = objectMapper.readValue(message, OrderRequest.class);
        System.out.println(orderInfo);


    }
}
