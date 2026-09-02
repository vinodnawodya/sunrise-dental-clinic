package com.sunrisedental.service;

import com.sunrisedental.entity.Appointment;
import com.sunrisedental.entity.Bill;
import com.sunrisedental.entity.Treatment;
import com.sunrisedental.entity.TreatmentCategory;
import com.sunrisedental.pattern.strategy.StandardTreatmentPricing;
import com.sunrisedental.pattern.strategy.SurgicalTreatmentPricing;
import com.sunrisedental.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private AppointmentService appointmentService;

    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(billRepository, appointmentService,
                new StandardTreatmentPricing(), new SurgicalTreatmentPricing());
    }

    @Test
    void generateBill_appliesSurgicalSurcharge_forSurgicalTreatments() {
        Treatment treatment = Treatment.builder().name("Root Canal").category(TreatmentCategory.SURGICAL).baseCost(new BigDecimal("450.00")).build();
        Appointment appointment = Appointment.builder().appointmentNumber("APT-000010").treatment(treatment).build();
        when(appointmentService.findByAppointmentNumber("APT-000010")).thenReturn(appointment);
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill bill = billingService.generateBill("APT-000010");

        assertThat(bill.getTotalCost()).isEqualByComparingTo("517.50");
    }

    @Test
    void generateBill_chargesBaseCost_forStandardTreatments() {
        Treatment treatment = Treatment.builder().name("Dental Cleaning").category(TreatmentCategory.STANDARD).baseCost(new BigDecimal("50.00")).build();
        Appointment appointment = Appointment.builder().appointmentNumber("APT-000011").treatment(treatment).build();
        when(appointmentService.findByAppointmentNumber("APT-000011")).thenReturn(appointment);
        ArgumentCaptor<Bill> billCaptor = ArgumentCaptor.forClass(Bill.class);
        when(billRepository.save(billCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        billingService.generateBill("APT-000011");

        assertThat(billCaptor.getValue().getTotalCost()).isEqualByComparingTo("50.00");
    }

    @Test
    void findByAppointmentNumber_throwsResourceNotFoundException_whenNoBillExists() {
        when(billRepository.findByAppointment_AppointmentNumber("APT-999999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> billingService.findByAppointmentNumber("APT-999999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
