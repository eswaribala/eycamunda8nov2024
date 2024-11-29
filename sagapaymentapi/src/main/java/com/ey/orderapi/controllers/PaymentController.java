package com.ey.orderapi.controllers;

import com.ey.orderapi.dtos.GenericResponse;
import com.ey.orderapi.services.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping("/v1.0")
    public CompletableFuture<ResponseEntity<String>>makePayment() throws JsonProcessingException {

        return  paymentService.makePayment()
                .thenApply(result->ResponseEntity.status(HttpStatus.OK)
                        .body(result.getRecordMetadata().topic()+","+result.getRecordMetadata().partition()+","+result.getRecordMetadata().offset()))
                .exceptionally(ex-> {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
                });
    }


}
