package com.ey.orderapi.configurations;

import com.github.javafaker.Faker;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class JobConfiguration {
    //@Autowired
   //private ZeebeClient zeebeClient;
    private Map<String,Object> map;
    @JobWorker(type = "generateOrderId",autoComplete = false)
    public Map<String,Long> handleOrderIdGeneration(final JobClient jobClient, ActivatedJob activatedJob){
        Faker faker=new Faker();
        log.info("Job Key"+activatedJob.getKey());
        Map<String,Long> map=new HashMap<>();
        map.put("OrderId",faker.number().numberBetween(1000L,1000000L));
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(map)
                .send()
                .exceptionally((throwable)->{
            throw new RuntimeException("Job not found");
        });
     return map;
    }

    @JobWorker(type = "showOrderId",autoComplete = false)
    public void showGeneratedId(final JobClient jobClient, ActivatedJob activatedJob){
       map= activatedJob.getVariablesAsMap();
       String orderId=map.get("OrderId").toString();
       log.info("Generated Order No"+orderId);


        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(map)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
    }


}
