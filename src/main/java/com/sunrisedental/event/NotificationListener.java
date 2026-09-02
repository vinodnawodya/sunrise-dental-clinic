package com.sunrisedental.event;

import com.sunrisedental.entity.Appointment;
import com.sunrisedental.service.NotificationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer: reacts to {@link AppointmentCreatedEvent} by simulating an
 * SMS/email confirmation to the patient (logged only - see
 * NotificationManager and README.md for the documented assumption).
 */
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationManager notificationManager;

    @EventListener
    public void onAppointmentCreated(AppointmentCreatedEvent event) {
        Appointment appointment = event.getAppointment();
        String message = String.format(
                "Your appointment %s with %s is scheduled for %s at %s.",
                appointment.getAppointmentNumber(),
                appointment.getDentist().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime());
        notificationManager.sendNotification(appointment.getPatient().getContactNumber(), message);
    }
}
