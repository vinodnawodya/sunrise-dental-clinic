package com.sunrisedental.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentFormDto {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Patient address is required")
    private String patientAddress;

    @NotBlank(message = "Patient contact number is required")
    @Pattern(regexp = "^[0-9+()\\-\\s]{7,20}$", message = "Contact number must be 7-20 digits, optionally with +, -, (), or spaces")
    private String patientContactNumber;

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
