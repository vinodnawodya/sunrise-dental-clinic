package com.sunrisedental.event;

import com.sunrisedental.entity.Appointment;
import org.springframework.context.ApplicationEvent;

/**
 * Observer pattern (Spring flavour): published via
 * {@link org.springframework.context.ApplicationEventPublisher} whenever an
 * appointment is saved. {@link NotificationListener} is the observer.
 */
public class AppointmentCreatedEvent extends ApplicationEvent {

    private final Appointment appointment;

    public AppointmentCreatedEvent(Object source, Appointment appointment) {
        super(source);
        this.appointment = appointment;
    }

    public Appointment getAppointment() {
        return appointment;
    }
}
