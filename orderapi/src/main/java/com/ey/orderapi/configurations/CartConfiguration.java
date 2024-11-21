package com.ey.orderapi.configurations;

import com.ey.orderapi.models.Product;
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
    private Map<String,Object> map;

    private static List<Map<String,Product>> productsMap =new ArrayList<>();
    private static long sequence=1;


    @JobWorker(type = "storeInArray", autoComplete = false)
    public List<Map<String, Product>> handleCartStorage(final JobClient jobClient,ActivatedJob activatedJob){

        map=activatedJob.getVariablesAsMap();
        Map<String,Product> productMap=new HashMap<>();
        map.entrySet().stream().forEach((entryset)->{
            System.out.println(entryset.getKey()+","+entryset.getValue());
        });

        Product product=new Product();
        product.setProductName(map.get("select_product").toString());
        product.setQty(Long.parseLong(map.get("textfield_quantity").toString()));
        productMap.put("product"+sequence,product);
        sequence++;
        productsMap.add(productMap);
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(productsMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });


        return productsMap;
    }



    @JobWorker(type = "computeTotalCost",autoComplete = false)
    public Map<String,Long> computeCostOfTheProduct(final JobClient jobClient, ActivatedJob activatedJob){

        map=activatedJob.getVariablesAsMap();
        map.entrySet().stream().forEach((entryset)->{
            System.out.println(entryset.getKey()+","+entryset.getValue());
        });
        List<Map<String,Product>> listOfProducts = (List<Map<String, Product>>) map.get("productsMap");
        long totalQty=0L;
        for(Map<String,Product> map: listOfProducts){
          totalQty+= map.entrySet().stream().map(p->Long.parseLong(p.getValue().toString())).reduce(0L,Long::sum);
        }

        long totalCost=totalQty*5000;
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
