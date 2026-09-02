package com.sunrisedental.repository;

import com.sunrisedental.entity.Appointment;
import com.sunrisedental.entity.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    boolean existsByDentistAndAppointmentDateAndAppointmentTime(
            Dentist dentist, LocalDate appointmentDate, LocalTime appointmentTime);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
}
