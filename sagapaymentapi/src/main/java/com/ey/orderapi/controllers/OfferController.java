package com.ey.orderapi.controllers;

import com.ey.orderapi.dtos.GenericResponse;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("offers")
public class OfferController {

    @Autowired
    private ZeebeClient zeebeClient;

    @GetMapping("/v1.0")
    public ResponseEntity<GenericResponse> startOfferProcessInstance(){

        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(ProcessConstant.BPMN_OFFER_PROCESS_ID)
                .latestVersion()
                .send()
                .exceptionally((throwable)-> {
                    throw new RuntimeException("Process Not Instantiated");
                });
         return ResponseEntity.status(HttpStatus.CREATED).body(new GenericResponse("Process Initiated"));
    }

}
