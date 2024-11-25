package com.ey.orderapi.configurations;

import com.ey.orderapi.exceptions.StockQtyNotAvailable;
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
    public Map<String,Boolean> addStockConfiguration(final JobClient jobClient, ActivatedJob activatedJob) throws JsonProcessingException {
          Map<String,Object> activatedMap=activatedJob.getVariablesAsMap();
          Stock stock=new Stock();
        Faker faker=new Faker();
        ObjectMapper objectMapper=new ObjectMapper();
        String variablesJson = activatedJob.getVariables();

        // Deserialize variables to a Map
        Map<String, Object> variables = objectMapper.readValue(variablesJson, new TypeReference<>() {});

        // Read the "items" variable (array of JSON objects)
        List<Map<String, Object>> items = (List<Map<String, Object>>) variables.get("products");

        Map<String, Boolean> mapStatus=new HashMap<>();

        for (Map<String, Object> item : items) {
            System.out.println("productId: " + item.get("productId"));
            long productId = Long.parseLong(item.get("productId").toString());
            stock.setProductId(Long.parseLong(item.get("productId").toString()));
            long qty=Long.parseLong(activatedMap.get("stock_quantity").toString());
            if(qty>0)
             stock.setAvailableQty(qty);
            else
                stock.setAvailableQty(new Faker().number().numberBetween(100L,1000L));
            stock.setLocation(faker.address().city());
            Stock stockObj = stockService.addStock(stock);
            if (stockObj != null) {
                mapStatus.put("StockStatus", true);
            }
            else {
                mapStatus.put("StockStatus", false);
            }
        }

            List<Boolean> successes= mapStatus.entrySet().stream().filter(entryset->entryset.getValue()==true)
                    .map(entrySet->entrySet.getValue())
                    .collect(Collectors.toList());

            if(successes.size()>0){

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
            long productId=Long.parseLong(item.get("productId").toString());
            if(checkAvailability(productId))
                stockAvailableMap.put(productId,true);
            else
                stockAvailableMap.put(productId,false);
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

     private boolean checkAvailability(long productId){
        Stock stockObj=null;
        boolean status=false;
         try {
         stockObj=stockService.isStockAvailable(productId);

             if (stockObj != null)
                 status=true;

         }catch (StockQtyNotAvailable ex){
             status=false;
         }

         return status;
     }


}
