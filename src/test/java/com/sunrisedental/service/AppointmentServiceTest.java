package com.sunrisedental.service;

import com.sunrisedental.entity.Dentist;
import com.sunrisedental.entity.Patient;
import com.sunrisedental.entity.Treatment;
import com.sunrisedental.entity.TreatmentCategory;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.PatientRepository;
import com.sunrisedental.repository.TreatmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import com.sunrisedental.entity.Appointment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TDD evidence: this test was written and committed failing (red) before
 * AppointmentService had any double-booking check, then AppointmentService
 * was updated to make it pass (green). See the two "TDD red"/"TDD green"
 * commits in the git history.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DentistRepository dentistRepository;
    @Mock
    private TreatmentRepository treatmentRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void rejectsAppointmentWhenDentistAlreadyBookedAtThatDateAndTime() {
        Patient patient = Patient.builder().id(1L).name("Alice Morgan").address("Cardiff").contactNumber("07700123456").build();
        Dentist dentist = Dentist.builder().id(1L).name("Dr. Sarah Lewis").specialization("General Dentistry").build();
        Treatment treatment = Treatment.builder().id(1L).name("Dental Cleaning").category(TreatmentCategory.STANDARD).baseCost(new BigDecimal("50.00")).build();
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(9, 30);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistAndAppointmentDateAndAppointmentTime(dentist, date, time))
                .thenReturn(true);

        assertThatThrownBy(() -> appointmentService.createAppointment(1L, 1L, 1L, date, time))
                .isInstanceOf(DentistDoubleBookingException.class);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_savesAndPublishesEvent_whenNoConflict() {
        Patient patient = Patient.builder().id(1L).name("Alice Morgan").address("Cardiff").contactNumber("07700123456").build();
        Dentist dentist = Dentist.builder().id(1L).name("Dr. Sarah Lewis").specialization("General Dentistry").build();
        Treatment treatment = Treatment.builder().id(1L).name("Dental Cleaning").category(TreatmentCategory.STANDARD).baseCost(new BigDecimal("50.00")).build();
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(9, 30);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistAndAppointmentDateAndAppointmentTime(dentist, date, time))
                .thenReturn(false);
        when(appointmentRepository.count()).thenReturn(0L);
        when(appointmentRepository.findByAppointmentNumber("APT-000001")).thenReturn(Optional.empty());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.createAppointment(1L, 1L, 1L, date, time);

        assertThat(result.getAppointmentNumber()).isEqualTo("APT-000001");
        assertThat(result.getStatus()).isEqualTo(com.sunrisedental.entity.AppointmentStatus.SCHEDULED);
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void findByAppointmentNumber_throwsResourceNotFoundException_whenMissing() {
        when(appointmentRepository.findByAppointmentNumber("APT-999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.findByAppointmentNumber("APT-999999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
