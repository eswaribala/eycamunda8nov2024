package com.ey.orderapi.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(topics = "eyordernov2024", groupId = "saga-ecommerce-group")
public class OrderListener {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaHandler(isDefault = true)
    public void receiveOrderEvent(String message){

        System.out.println(message);
    }
}
