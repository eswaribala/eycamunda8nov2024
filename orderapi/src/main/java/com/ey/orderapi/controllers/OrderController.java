package com.ey.orderapi.controllers;

import com.ey.orderapi.dtos.GenericResponse;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("orders")
@Slf4j
public class OrderController {

    @Autowired
    private ZeebeClient zeebeClient;

    @PostMapping("/v1.0")
    public ResponseEntity<GenericResponse> startOrderProcess(@RequestBody Map<String,Object> variables){
        zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId(ProcessConstant.BPMN_PROCESS_ID)
                .latestVersion()
                .variables(variables)
                .send();

        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse("Process Started"+ProcessConstant.BPMN_PROCESS_ID+"Successfully"));

    }

    @GetMapping("/v1.0")
    public ResponseEntity<GenericResponse> triggerSubProcess(){

        zeebeClient.newPublishMessageCommand()
                .messageName("Message_Inventory")
                .correlationKey("1001")
                .messageId("Event_Inventory_Message")
                .timeToLive(Duration.ofMinutes(10))
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + zeebeClient, throwable);
                });
        log.info("Message received");
        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse("Message Delivered"));

    }

}
