package com.ey.orderapi.configurations;

import com.ey.orderapi.models.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Configuration
@Slf4j
public class KafkaPublishConfiguration {

    @Autowired
    private KafkaTemplate<Object,Object> kafkaTemplate;

    @Value("${topicName}")
    private String topicName;

    @JobWorker(type = "kafkaPublish",autoComplete = false)
    public CompletableFuture<Map<String, String>> publishOrder(final JobClient jobClient, ActivatedJob activatedJob) throws JsonProcessingException {


        ObjectMapper objectMapper=new ObjectMapper();
        String variablesJson = activatedJob.getVariables();

        // Deserialize variables to a Map
        Map<String, Object> variables = objectMapper.readValue(variablesJson, new TypeReference<>() {});

        // Read the "items" variable (array of JSON objects)
        Map<String, Object> item = (Map<String, Object>) variables.get("Order");
          long orderId=Long.parseLong(item.get("orderId").toString());
        long orderAmount=Long.parseLong(item.get("orderAmount").toString());
        LocalDate orderDate=LocalDate.parse(item.get("orderDate").toString());

        Order order=Order.builder()
                .orderId(orderId)
                .orderAmount(orderAmount)
               .orderDate(orderDate)
                .build();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(order);
        Map<String,String> kafkaResult=new HashMap<>();
       return kafkaTemplate.send(topicName,json).thenApply(result-> {
           kafkaResult.put("result", result.getRecordMetadata().topic() + "," +
                   result.getRecordMetadata().partition());
           kafkaResult.put("result", "published....");
           jobClient.newCompleteCommand(activatedJob.getKey())
                   .variables(kafkaResult)
                   .send()
                   .exceptionally((throwable) -> {
                       throw new RuntimeException("Job not found");
                   });
           return kafkaResult;

       }).exceptionally(ex-> {
            kafkaResult.put("result",ex.getMessage());
            return kafkaResult;
        });

    }

}
