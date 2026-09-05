-- Sunrise Dental Clinic - MySQL 8 schema
-- Canonical schema for the assignment brief. Applied when the app runs with
-- the "mysql" Spring profile (see application-mysql.properties / README.md).
--
-- Tables use CREATE TABLE IF NOT EXISTS rather than DROP+CREATE, so data
-- entered through the running app survives a restart - spring.sql.init.mode
-- =always still runs this script on every startup as the brief requires, it
-- just no longer wipes existing data once a table is already there.

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS patients (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    address        VARCHAR(255),
    contact_number VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS dentists (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    specialization VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS treatments (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100)   NOT NULL,
    category  VARCHAR(30)    NOT NULL,
    base_cost DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointments (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_number  VARCHAR(20) NOT NULL UNIQUE,
    patient_id          BIGINT      NOT NULL,
    dentist_id          BIGINT      NOT NULL,
    treatment_id        BIGINT      NOT NULL,
    appointment_date    DATE        NOT NULL,
    appointment_time    TIME        NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_appointment_dentist FOREIGN KEY (dentist_id) REFERENCES dentists (id),
    CONSTRAINT fk_appointment_treatment FOREIGN KEY (treatment_id) REFERENCES treatments (id),
    CONSTRAINT uq_dentist_datetime UNIQUE (dentist_id, appointment_date, appointment_time)
);

CREATE TABLE IF NOT EXISTS bills (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT         NOT NULL UNIQUE,
    total_cost     DECIMAL(10, 2) NOT NULL,
    generated_date DATETIME       NOT NULL,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
);

CREATE TABLE IF NOT EXISTS audit_log (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT,
    action         VARCHAR(50)  NOT NULL,
    details        VARCHAR(255),
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- View: today's appointments, joined for the reporting dashboard.
CREATE OR REPLACE VIEW daily_appointments_view AS
SELECT
    a.id                    AS appointment_id,
    a.appointment_number,
    a.appointment_date,
    a.appointment_time,
    a.status,
    p.id                    AS patient_id,
    p.name                  AS patient_name,
    p.contact_number        AS patient_contact,
    d.id                    AS dentist_id,
    d.name                  AS dentist_name,
    d.specialization        AS dentist_specialization,
    t.name                  AS treatment_name,
    t.category              AS treatment_category,
    t.base_cost
FROM appointments a
JOIN patients p   ON a.patient_id = p.id
JOIN dentists d   ON a.dentist_id = d.id
JOIN treatments t ON a.treatment_id = t.id
WHERE a.appointment_date = CURDATE();

-- Stored procedure: total cost for a given appointment.
DROP PROCEDURE IF EXISTS CalculateAppointmentBill;

DELIMITER $$
CREATE PROCEDURE CalculateAppointmentBill(
    IN  p_appointment_id BIGINT,
    OUT p_total_cost     DECIMAL(10, 2)
)
BEGIN
    SELECT t.base_cost
    INTO p_total_cost
    FROM appointments a
    JOIN treatments t ON a.treatment_id = t.id
    WHERE a.id = p_appointment_id;
END$$
DELIMITER ;

-- Trigger: write an audit_log row whenever an appointment is created.
DROP TRIGGER IF EXISTS after_appointment_insert;

DELIMITER $$
CREATE TRIGGER after_appointment_insert
AFTER INSERT ON appointments
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (appointment_id, action, details)
    VALUES (NEW.id, 'APPOINTMENT_CREATED',
            CONCAT('Appointment ', NEW.appointment_number, ' created for patient_id=', NEW.patient_id));
END$$
DELIMITER ;
