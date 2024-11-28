package com.ey.orderapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Appointment {
    private long appointmentNo;
    private long mobileNo;
    private boolean requestStatus;
}
