package com.ey.orderapi.configurations;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class CartConfiguration {

   // @Autowired
   // private ZeebeClient zeebeClient;
    private Map<String,Object> map;
    @JobWorker(type = "computeTotalCost",autoComplete = false)
    public Map<String,Long> computeCostOfTheProduct(final JobClient jobClient, ActivatedJob activatedJob){

        map=activatedJob.getVariablesAsMap();
        map.entrySet().stream().forEach((entryset)->{
            System.out.println(entryset.getKey()+","+entryset.getValue());
        });
        long totalCost=Long.parseLong(map.get("textfield_quantity").toString()) *5000;
        Map<String,Long> computedCostMap=new HashMap<>();
        computedCostMap.put("TotalCost",totalCost);

        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(computedCostMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
          return computedCostMap;

    }
}
