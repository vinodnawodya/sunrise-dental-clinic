package com.sunrisedental.controller.api;

import com.sunrisedental.dto.AppointmentFormDto;
import com.sunrisedental.dto.AppointmentResponseDto;
import com.sunrisedental.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentApiController {

    private final AppointmentService appointmentService;

    @GetMapping("/{appointmentNumber}")
    public AppointmentResponseDto getByNumber(@PathVariable String appointmentNumber) {
        return AppointmentResponseDto.from(appointmentService.findByAppointmentNumber(appointmentNumber));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDto create(@Valid @RequestBody AppointmentFormDto request) {
        var appointment = appointmentService.createAppointment(
                request.getPatientName(),
                request.getPatientAddress(),
                request.getPatientContactNumber(),
                request.getDentistId(),
                request.getTreatmentId(),
                request.getAppointmentDate(),
                request.getAppointmentTime());
        return AppointmentResponseDto.from(appointment);
    }
}
