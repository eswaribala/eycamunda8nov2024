package com.ey.orderapi.configurations;

import com.ey.orderapi.services.StockService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import com.ey.orderapi.models.Stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class StockConfiguration {

    @Autowired
    private StockService stockService;

    @JobWorker(type = "storeStock",autoComplete = false)
    public Map<String,Boolean> addStockConfiguration(final JobClient jobClient, ActivatedJob activatedJob){
          Map<String,Object> activatedMap=activatedJob.getVariablesAsMap();
          Stock stock=new Stock();
        Faker faker=new Faker();
          stock.setProductId(Long.parseLong(activatedMap.get("stock_product_id").toString()));
          stock.setAvailableQty(Long.parseLong(activatedMap.get("stock_quantity").toString()));
          stock.setLocation(faker.address().city());
          Stock stockObj=stockService.addStock(stock);
          Map<String, Boolean> mapStatus=new HashMap<>();
           if(stockObj!=null) {
               mapStatus.put("StockStatus", true);
               jobClient.newCompleteCommand(activatedJob.getKey())
                       .variables(mapStatus)
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
        return mapStatus;


    }


    @JobWorker(type = "isStockAvailable",autoComplete = false)
    public Map<String,Boolean> isStockAvailable(final JobClient jobClient, ActivatedJob activatedJob) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        String variablesJson = activatedJob.getVariables();

        // Deserialize variables to a Map
        Map<String, Object> variables = objectMapper.readValue(variablesJson, new TypeReference<>() {});

        // Read the "items" variable (array of JSON objects)
        List<Map<String, Object>> items = (List<Map<String, Object>>) variables.get("products");

        long totalQty=0;
        Stock stockObj=null;
        // Process the array of items
        Map<Long, Boolean> stockAvailableMap=new HashMap<>();
        for (Map<String, Object> item : items) {
            System.out.println("productId: " + item.get("productId"));
             stockObj=stockService.isStockAvailable(Long.parseLong(item.get("productId").toString()));
             if(stockObj!=null)
                 stockAvailableMap.put(stockObj.getProductId(),true);
             else
                 stockAvailableMap.put(stockObj.getProductId(),false);
        }

       List<Boolean> failures= stockAvailableMap.entrySet().stream().filter(entryset->entryset.getValue()==false)
               .map(entrySet->entrySet.getValue())
               .collect(Collectors.toList());
        Map<String, Boolean> stockAvailableStatusMap=new HashMap<>();
        if(failures.size()>0)
            stockAvailableStatusMap.put("StockStatus",false);
        else
            stockAvailableStatusMap.put("StockStatus",true);


        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(stockAvailableStatusMap)
                .send()
                .exceptionally((throwable) -> {
                    throw new RuntimeException("Job not found");
                });

       return stockAvailableStatusMap;

    }


}
