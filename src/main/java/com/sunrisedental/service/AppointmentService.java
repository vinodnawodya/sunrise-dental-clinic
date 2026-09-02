package com.sunrisedental.service;

import com.sunrisedental.entity.Appointment;
import com.sunrisedental.entity.AppointmentStatus;
import com.sunrisedental.entity.Dentist;
import com.sunrisedental.entity.Patient;
import com.sunrisedental.entity.Treatment;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.PatientRepository;
import com.sunrisedental.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;

    @Transactional
    public Appointment createAppointment(Long patientId, Long dentistId, Long treatmentId,
                                          LocalDate appointmentDate, LocalTime appointmentTime) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        Dentist dentist = dentistRepository.findById(dentistId)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found: " + dentistId));
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found: " + treatmentId));

        Appointment appointment = Appointment.builder()
                .appointmentNumber(generateAppointmentNumber())
                .patient(patient)
                .dentist(dentist)
                .treatment(treatment)
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return appointmentRepository.save(appointment);
    }

    public Appointment findByAppointmentNumber(String appointmentNumber) {
        return appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + appointmentNumber));
    }

    public List<Appointment> findByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }

    private String generateAppointmentNumber() {
        long sequence = appointmentRepository.count() + 1;
        String candidate;
        do {
            candidate = String.format("APT-%06d", sequence++);
        } while (appointmentRepository.findByAppointmentNumber(candidate).isPresent());
        return candidate;
    }
}
