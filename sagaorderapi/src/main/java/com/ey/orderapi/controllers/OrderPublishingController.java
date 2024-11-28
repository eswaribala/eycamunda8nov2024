package com.ey.orderapi.controllers;


import com.ey.orderapi.dtos.GenericResponse;
import com.ey.orderapi.dtos.OrderRequest;
import com.ey.orderapi.models.Order;
import com.ey.orderapi.services.OrderService;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("orders")
public class OrderPublishingController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ZeebeClient zeebeClient;

    @PostMapping("/v1.0")
    public ResponseEntity<GenericResponse> createProcessInstance(@RequestBody OrderRequest orderRequest){

        Order order= Order.builder()
                .orderDate(orderRequest.getOrderDate())
                .orderAmount(orderRequest.getOrderAmount())
                .build();
        Order orderObj=orderService.addOrder(order);
        Map<String,Order> mappedInstance=new HashMap<>();


        if(orderObj!=null){
            mappedInstance.put("Order",orderObj);
            zeebeClient
                    .newCreateInstanceCommand()
                    .bpmnProcessId(ProcessConstant.BPMN_PROCESS_ID)
                    .latestVersion()
                    .variables(mappedInstance)
                    .send();


        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericResponse("Message Published to Orchestrator...."));

    }

}
