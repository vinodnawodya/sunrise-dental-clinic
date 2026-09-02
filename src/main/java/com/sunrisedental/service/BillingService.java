package com.sunrisedental.service;

import com.sunrisedental.entity.Appointment;
import com.sunrisedental.entity.Bill;
import com.sunrisedental.entity.TreatmentCategory;
import com.sunrisedental.pattern.factory.BillFactory;
import com.sunrisedental.pattern.strategy.BillingStrategy;
import com.sunrisedental.pattern.strategy.StandardTreatmentPricing;
import com.sunrisedental.pattern.strategy.SurgicalTreatmentPricing;
import com.sunrisedental.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final AppointmentService appointmentService;
    private final StandardTreatmentPricing standardTreatmentPricing;
    private final SurgicalTreatmentPricing surgicalTreatmentPricing;

    @Transactional
    public Bill generateBill(String appointmentNumber) {
        Appointment appointment = appointmentService.findByAppointmentNumber(appointmentNumber);
        BillingStrategy billingStrategy = strategyFor(appointment.getTreatment().getCategory());
        Bill bill = BillFactory.createBill(appointment, billingStrategy);
        return billRepository.save(bill);
    }

    public Bill findByAppointmentNumber(String appointmentNumber) {
        return billRepository.findByAppointment_AppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found for appointment: " + appointmentNumber));
    }

    public List<BillRepository.RevenueByTreatment> revenueByTreatment() {
        return billRepository.revenueByTreatment();
    }

    private BillingStrategy strategyFor(TreatmentCategory category) {
        return switch (category) {
            case STANDARD -> standardTreatmentPricing;
            case SURGICAL -> surgicalTreatmentPricing;
        };
    }
}
