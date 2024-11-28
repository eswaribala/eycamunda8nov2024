package com.ey.orderapi.configurations;

import com.ey.orderapi.dtos.GenericResponse;
import com.ey.orderapi.models.Order;
import com.ey.orderapi.services.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class InventoryConfiguration {

    @Autowired
    private ZeebeClient zeebeClient;
    @Autowired
    private OrderService orderService;

    Map<String, Order> computedCostMap;
    @JobWorker(type = "dbstore",autoComplete = false)
public Map<String, Order> saveOrder(final JobClient jobClient, ActivatedJob activatedJob) throws JsonProcessingException {

        log.info("Sub process calls Db Store....");
        ObjectMapper objectMapper=new ObjectMapper();
    String variablesJson = activatedJob.getVariables();
    // Deserialize variables to a Map
    Map<String, Object> variables = objectMapper.readValue(variablesJson, new TypeReference<>() {});
    variables.entrySet().stream().forEach(entryset->{
        log.info("Db Store"+","+entryset.getKey()+","+entryset.getValue());
    });


    if(variables.get("TotalCost")!=null) {
        long totalCost = Long.parseLong(variables.get("TotalCost").toString());

        // Read the "items" variable (array of JSON objects)
        List<Map<String, Object>> items = (List<Map<String, Object>>) variables.get("products");

        Order order = new Order();
        order.setOrderAmount(totalCost);
        order.setOrderDate(LocalDate.now());
        Order orderResponse = orderService.addOrder(order);

        computedCostMap= new HashMap<>();
        computedCostMap.put("OrderData", orderResponse);

        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(computedCostMap)
                .send()
                .exceptionally((throwable) -> {
                    throw new RuntimeException("Job not found");
                });
    }
    else{
        jobClient.newCompleteCommand(activatedJob.getKey())

                .send()
                .exceptionally((throwable) -> {
                    throw new RuntimeException("Job not found");
                });
    }
    return computedCostMap;
}

    @JobWorker(type = "notifyOrderCompletion",autoComplete = false)
    public HashMap<String,Boolean>  notifyOrderCompletion(final JobClient jobClient, ActivatedJob activatedJob) {
        HashMap<String,Boolean> notify=new HashMap<>();
        notify.put("OrderStored", true);
        zeebeClient.newPublishMessageCommand()
                .messageName("Message_OrderStore")
                .correlationKey("1002")
                .variables(notify)
                .timeToLive(Duration.ofMinutes(3))
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(notify)
                .send()
                .exceptionally((throwable) -> {
                    throw new RuntimeException("Job not found");
                });
        log.info("Message received");
        return notify;

    }




}
