package com.sunrisedental.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentFormDto {

    @NotNull(message = "Please select a patient")
    private Long patientId;

    @NotNull(message = "Please select a dentist")
    private Long dentistId;

    @NotNull(message = "Please select a treatment")
    private Long treatmentId;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;
}
