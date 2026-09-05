package com.sunrisedental.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class NotificationManager {

    private static NotificationManager instance;

    private NotificationManager() {
        instance = this;
    }

    public static NotificationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NotificationManager has not been initialized by Spring yet");
        }
        return instance;
    }

    /**
     * Simulates sending an SMS/email. No real provider is integrated for
     * this assignment (documented assumption in README.md) - the
     * notification is just logged.
     */
    public void sendNotification(String recipientContact, String message) {
        log.info("[SIMULATED SMS/EMAIL] To: {} | Message: {}", recipientContact, message);
    }
}
