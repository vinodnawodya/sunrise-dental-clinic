package com.sunrisedental.dto;

import com.sunrisedental.entity.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillResponseDto(
        String appointmentNumber,
        String patientName,
        String treatmentName,
        BigDecimal totalCost,
        LocalDateTime generatedDate
) {
    public static BillResponseDto from(Bill bill) {
        return new BillResponseDto(
                bill.getAppointment().getAppointmentNumber(),
                bill.getAppointment().getPatient().getName(),
                bill.getAppointment().getTreatment().getName(),
                bill.getTotalCost(),
                bill.getGeneratedDate());
    }
}
