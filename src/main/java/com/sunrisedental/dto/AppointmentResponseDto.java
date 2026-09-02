package com.sunrisedental.dto;

import com.sunrisedental.entity.Appointment;
import com.sunrisedental.entity.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponseDto(
        String appointmentNumber,
        String patientName,
        String dentistName,
        String treatmentName,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        AppointmentStatus status
) {
    public static AppointmentResponseDto from(Appointment appointment) {
        return new AppointmentResponseDto(
                appointment.getAppointmentNumber(),
                appointment.getPatient().getName(),
                appointment.getDentist().getName(),
                appointment.getTreatment().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus());
    }
}
