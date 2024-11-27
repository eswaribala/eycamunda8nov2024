package com.ey.orderapi.configurations;


import com.ey.orderapi.dtos.Appointment;
import com.ey.orderapi.services.AppointmentService;
import com.github.javafaker.Faker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@Slf4j
public class AppointmentConfiguration {

    private static long sequenceNo=1;

    @Autowired
    private AppointmentService appointmentService;

    @JobWorker(type = "generateAppointmentNo",autoComplete = false)
    public Map<String,Long> generateAppointmentNo(final JobClient jobClient, ActivatedJob activatedJob){

        log.info("Job Key"+activatedJob.getKey());
        Map<String,Long> map=new HashMap<>();
        map.put("appointmentNo",sequenceNo);
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(map)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        sequenceNo++;
        return map;

    }

    @JobWorker(type = "showAppointments",autoComplete = false)
    public Map<String, Set<Appointment>> receiveAllAppointments(final JobClient jobClient, ActivatedJob activatedJob){

        Map<String,Object> activatedMap=activatedJob.getVariablesAsMap();
        Appointment appointment=new Appointment();
        appointment.setAppointmentNo(Long.parseLong(activatedMap.get("appointmentNo").toString()));
        appointment.setMobileNo(Long.parseLong(activatedMap.get("mobileNo").toString()));
        appointment.setRequestStatus(Boolean.parseBoolean(activatedMap.get("request").toString()));
        appointmentService.addAppointment(appointment);
        Map<String, Set<Appointment>> appointmentsMap=new HashMap<>();
        appointmentsMap.put("appointments",appointmentService.getAllAppointments());

        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(appointmentsMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        return appointmentsMap;
    }

}
