package com.ey.orderapi.controllers;

import com.ey.orderapi.dtos.GenericResponse;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("orders")
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
}
