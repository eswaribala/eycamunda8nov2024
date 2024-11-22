package com.ey.orderapi.configurations;


import com.ey.orderapi.dtos.Product;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class CartConfiguration {

   // @Autowired
   // private ZeebeClient zeebeClient;
    private static Map<String,Object> map;

    private static List<Product>listOfProducts=new ArrayList<>();

    private static long sequence=1;

    @JobWorker(type = "storeInArray", autoComplete = false)
    public Map<String,List<Product>> handleCartStorage(final JobClient jobClient,ActivatedJob activatedJob){

        map=activatedJob.getVariablesAsMap();
        Map<String,List<Product>> productsMap=new HashMap<>();
        map.entrySet().stream().forEach((entryset)->{
            System.out.println(entryset.getKey()+","+entryset.getValue());
        });

        Product product=new Product();
        product.setProductId(sequence);
        product.setProductName(map.get("select_product").toString());
        product.setQty(Long.parseLong(map.get("textfield_quantity").toString()));

        listOfProducts.add(product);
        productsMap.put("products",listOfProducts);
        sequence++;
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(productsMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });


        return productsMap;
    }



    @JobWorker(type = "computeTotalCost",autoComplete = false)
    public Map<String,Long> computeCostOfTheProduct(final JobClient jobClient, ActivatedJob activatedJob) throws JsonProcessingException {

       /* map=activatedJob.getVariablesAsMap();
        map.entrySet().stream().forEach((entryset)->{
            System.out.println(entryset.getKey()+","+entryset.getValue());
        });
       List<Product> products = (List<Product>) map.get("products");

        long totalQty = products.stream() // Stream of products
                .mapToLong(Product::getQty) // Extract quantities as int stream
                .sum(); // Sum the quantities*/

        ObjectMapper objectMapper=new ObjectMapper();
        String variablesJson = activatedJob.getVariables();

        // Deserialize variables to a Map
        Map<String, Object> variables = objectMapper.readValue(variablesJson, new TypeReference<>() {});

        // Read the "items" variable (array of JSON objects)
        List<Map<String, Object>> items = (List<Map<String, Object>>) variables.get("products");

        long totalQty=0;
        // Process the array of items
        for (Map<String, Object> item : items) {
            System.out.println("qty: " + item.get("qty"));
            totalQty+=Long.parseLong(item.get("qty").toString());

        }

        long totalCost= totalQty *5000;
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
