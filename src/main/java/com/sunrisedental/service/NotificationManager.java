package com.sunrisedental.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Singleton pattern.
 *
 * <p>As a Spring {@code @Component} this bean is already a singleton by
 * virtue of Spring's default bean scope ("singleton") - the container
 * creates exactly one instance and hands out that same instance to every
 * {@code @Autowired}/constructor-injected consumer. That alone would satisfy
 * the requirement in most real Spring codebases.
 *
 * <p>To make the pattern explicit rather than implicit, this class also
 * implements the classic Gang-of-Four shape: a private constructor plus a
 * static {@link #getInstance()} accessor. Spring is still the only place a
 * {@code NotificationManager} is ever constructed (it instantiates
 * package-private/private constructors via reflection), so {@code
 * getInstance()} simply returns that same Spring-managed instance - the two
 * mechanisms enforce the same one-instance guarantee from different
 * directions.
 */
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
