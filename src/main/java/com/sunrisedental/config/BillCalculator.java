package com.sunrisedental.config;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * H2-only stand-in for the MySQL stored procedure {@code CalculateAppointmentBill}
 * defined in db/schema-mysql.sql. H2 has no BEGIN..END procedure bodies, so
 * db/schema-h2.sql registers this method as a callable ALIAS instead:
 * {@code CALL CalculateAppointmentBill(<appointment_id>)}. Same query, same
 * result, different plumbing per database.
 */
public final class BillCalculator {

    private BillCalculator() {
    }

    public static BigDecimal calculateAppointmentBill(Connection connection, long appointmentId) throws SQLException {
        String sql = """
                SELECT t.base_cost
                FROM appointments a
                JOIN treatments t ON a.treatment_id = t.id
                WHERE a.id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, appointmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1);
                }
                return null;
            }
        }
    }
}
