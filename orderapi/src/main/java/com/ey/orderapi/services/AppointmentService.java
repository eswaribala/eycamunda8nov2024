package com.ey.orderapi.services;

import com.ey.orderapi.dtos.Appointment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private static Set<Appointment> appointmentList=new HashSet<>();

    public Appointment addAppointment(Appointment appointment){
          appointmentList.add(appointment);
          return appointment;
    }

    public Set<Appointment> getAllAppointments(){
        return appointmentList;
    }

    public Appointment getAppointmentByMobileNo(long mobileNo){
        return appointmentList.stream().filter(a->a.getMobileNo()==mobileNo)
                .findFirst().orElseThrow(()->new RuntimeException("MobileNo Not Available"));
    }

}
