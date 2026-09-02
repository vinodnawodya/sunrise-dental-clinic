package com.sunrisedental.repository;

import com.sunrisedental.entity.Appointment;
import com.sunrisedental.entity.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient
            JOIN FETCH a.dentist
            JOIN FETCH a.treatment
            WHERE a.appointmentNumber = :appointmentNumber
            """)
    Optional<Appointment> findByAppointmentNumber(@Param("appointmentNumber") String appointmentNumber);

    boolean existsByDentistAndAppointmentDateAndAppointmentTime(
            Dentist dentist, LocalDate appointmentDate, LocalTime appointmentTime);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    /** Backed by the daily_appointments_view SQL view (see db/schema-*.sql). */
    @Query(value = "SELECT * FROM daily_appointments_view", nativeQuery = true)
    List<DailyAppointmentView> findTodaysAppointments();

    interface DailyAppointmentView {
        String getAppointmentNumber();
        String getPatientName();
        String getPatientContact();
        String getDentistName();
        String getTreatmentName();
        LocalDate getAppointmentDate();
        LocalTime getAppointmentTime();
        String getStatus();
    }
}
