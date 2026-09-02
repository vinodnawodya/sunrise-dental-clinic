package com.sunrisedental.config;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * H2-only stand-in for the MySQL trigger {@code after_appointment_insert}
 * defined in db/schema-mysql.sql. H2 triggers are registered as Java classes
 * rather than SQL trigger bodies - see db/schema-h2.sql:
 * {@code CREATE TRIGGER after_appointment_insert AFTER INSERT ON appointments
 * FOR EACH ROW CALL "com.sunrisedental.config.AuditLogTrigger"}. Same effect
 * (one audit_log row per new appointment), different plumbing per database.
 */
public class AuditLogTrigger implements Trigger {

    private static final int APPOINTMENT_ID_COLUMN = 0;
    private static final int APPOINTMENT_NUMBER_COLUMN = 1;
    private static final int PATIENT_ID_COLUMN = 2;

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        Long appointmentId = (Long) newRow[APPOINTMENT_ID_COLUMN];
        String appointmentNumber = (String) newRow[APPOINTMENT_NUMBER_COLUMN];
        Long patientId = (Long) newRow[PATIENT_ID_COLUMN];

        String sql = "INSERT INTO audit_log (appointment_id, action, details) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, appointmentId);
            statement.setString(2, "APPOINTMENT_CREATED");
            statement.setString(3, "Appointment " + appointmentNumber + " created for patient_id=" + patientId);
            statement.executeUpdate();
        }
    }
}
