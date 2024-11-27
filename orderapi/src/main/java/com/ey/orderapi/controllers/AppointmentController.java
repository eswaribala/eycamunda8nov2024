package com.ey.orderapi.controllers;

import com.ey.orderapi.configurations.AppointmentConfiguration;
import com.ey.orderapi.dtos.Appointment;
import com.ey.orderapi.dtos.GenericResponse;
import com.ey.orderapi.services.AppointmentService;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("appointments")
@Slf4j
public class AppointmentController {

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/v1.0")
    public ResponseEntity<GenericResponse> appointmentProcessInstance(){

        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(ProcessConstant.BPMN_Appointment_PROCESS_ID)
                .latestVersion()
                .send()
                .exceptionally((throwable)-> {
                    throw new RuntimeException("Process Not Instantiated");
                });
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericResponse("Appointment Process Initiated"));
    }

    @GetMapping("/v1.0/appointments")
    public  ResponseEntity<GenericResponse> getAllAppointments(){

        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse(appointmentService.getAllAppointments()));
    }

    @GetMapping("/v1.0/{mobileNo}")
    public  ResponseEntity<GenericResponse> getAppointmentByMobileNo(@PathVariable("mobileNo") long mobileNo){

        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse(appointmentService.getAppointmentByMobileNo(mobileNo)));
    }

    @GetMapping("/v1.0/acceptorreject")
    public ResponseEntity<GenericResponse> appointmentAcceptedOrRejected(){
        HashMap<String,Boolean> acceptedMap=new HashMap<>();
        acceptedMap.put("AcceptedEvent", true);
        HashMap<String,Boolean> rejectedMap=new HashMap<>();
        rejectedMap.put("RejectedEvent", true);
        Set<Appointment> appointmentList=appointmentService.getAllAppointments();

        if(appointmentList.size()<50) {
            zeebeClient.newPublishMessageCommand()
                    .messageName("Message_Accepted")
                    .correlationKey("2001")

                    .variables(acceptedMap)
                    .timeToLive(Duration.ofMinutes(3))
                    .send()

                    .exceptionally((throwable) -> {
                        throw new RuntimeException("Job not found");
                    });
        }else{


            zeebeClient.newPublishMessageCommand()
                    .messageName("Message_Rejected")
                    .correlationKey("2002")

                    .variables(rejectedMap)
                    .timeToLive(Duration.ofMinutes(3))
                    .send()

                    .exceptionally((throwable)->{
                        throw new RuntimeException("Job not found");
                    });

        }


        log.info("Message Sent");
        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse("Accepted Message Delivered"));

    }






}
