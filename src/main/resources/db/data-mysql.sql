-- Seed data for MySQL. Staff login credentials are documented in README.md.
-- Password hash below is BCrypt for the plaintext password documented there
-- (username: admin, password: Sunrise123!).
--
-- Each INSERT only fires when its table is still empty, so data entered
-- through the running app is never overwritten by a restart - see
-- schema-mysql.sql for the matching CREATE TABLE IF NOT EXISTS. Requires
-- MySQL 8.0.19+ for the VALUES ROW(...) table value constructor.

INSERT INTO users (username, password_hash, role)
SELECT * FROM (VALUES
    ROW('admin', '$2a$10$g1mM/rwTz8IiS8jaeRvW9.uyODa9vfu8qRzapYGUpEpEO30h4M1GO', 'STAFF')
) AS v(username, password_hash, role)
WHERE NOT EXISTS (SELECT 1 FROM users);

INSERT INTO patients (name, address, contact_number)
SELECT * FROM (VALUES
    ROW('Kasun Perera', '45 Galle Road, Colombo 03', '0771234567'),
    ROW('Nimali Fernando', '12 Kandy Road, Kadawatha', '0712345678'),
    ROW('Chathura Bandara', '8 Temple Lane, Kandy', '0765432109')
) AS v(name, address, contact_number)
WHERE NOT EXISTS (SELECT 1 FROM patients);

INSERT INTO dentists (name, specialization)
SELECT * FROM (VALUES
    ROW('Dr. Priyantha Jayasuriya', 'General Dentistry'),
    ROW('Dr. Anoma Wickramasinghe', 'Oral Surgery')
) AS v(name, specialization)
WHERE NOT EXISTS (SELECT 1 FROM dentists);

INSERT INTO treatments (name, category, base_cost)
SELECT * FROM (VALUES
    ROW('Dental Cleaning', 'STANDARD', 4500.00),
    ROW('Cavity Filling', 'STANDARD', 8500.00),
    ROW('Root Canal', 'SURGICAL', 35000.00),
    ROW('Tooth Extraction', 'SURGICAL', 12000.00)
) AS v(name, category, base_cost)
WHERE NOT EXISTS (SELECT 1 FROM treatments);

INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status)
SELECT * FROM (VALUES
    ROW('APT-000001', 1, 1, 1, CURDATE(), '09:30:00', 'SCHEDULED'),
    ROW('APT-000002', 2, 2, 3, CURDATE(), '11:00:00', 'SCHEDULED')
) AS v(appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status)
WHERE NOT EXISTS (SELECT 1 FROM appointments);
