package com.sunrisedental.repository;

import com.sunrisedental.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByAppointment_AppointmentNumber(String appointmentNumber);

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
