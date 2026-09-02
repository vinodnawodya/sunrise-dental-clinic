package com.sunrisedental.repository;

import com.sunrisedental.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("""
            SELECT b FROM Bill b
            JOIN FETCH b.appointment a
            JOIN FETCH a.patient
            JOIN FETCH a.dentist
            JOIN FETCH a.treatment
            WHERE a.appointmentNumber = :appointmentNumber
            """)
    Optional<Bill> findByAppointment_AppointmentNumber(@Param("appointmentNumber") String appointmentNumber);

    @Query("""
            SELECT b.appointment.treatment.name AS treatmentName, SUM(b.totalCost) AS revenue
            FROM Bill b
            GROUP BY b.appointment.treatment.name
            ORDER BY revenue DESC
            """)
    java.util.List<RevenueByTreatment> revenueByTreatment();

    interface RevenueByTreatment {
        String getTreatmentName();
        java.math.BigDecimal getRevenue();
    }
}
