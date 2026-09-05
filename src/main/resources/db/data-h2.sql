-- Seed data for H2 (dev/sandbox fallback). Mirrors db/data-mysql.sql.
-- Staff login credentials are documented in README.md
-- (username: admin, password: Sunrise123!).
--
-- Each INSERT only fires when its table is still empty, so data entered
-- through the running app (patients registered via the UI, new
-- appointments, etc.) is never overwritten by a restart - see
-- schema-h2.sql for the matching CREATE TABLE IF NOT EXISTS.

INSERT INTO users (username, password_hash, role)
SELECT * FROM (VALUES
    ('admin', '$2a$10$g1mM/rwTz8IiS8jaeRvW9.uyODa9vfu8qRzapYGUpEpEO30h4M1GO', 'STAFF')
) AS v(username, password_hash, role)
WHERE NOT EXISTS (SELECT 1 FROM users);

INSERT INTO patients (name, address, contact_number)
SELECT * FROM (VALUES
    ('Kasun Perera', '45 Galle Road, Colombo 03', '0771234567'),
    ('Nimali Fernando', '12 Kandy Road, Kadawatha', '0712345678'),
    ('Chathura Bandara', '8 Temple Lane, Kandy', '0765432109')
) AS v(name, address, contact_number)
WHERE NOT EXISTS (SELECT 1 FROM patients);

INSERT INTO dentists (name, specialization)
SELECT * FROM (VALUES
    ('Dr. Priyantha Jayasuriya', 'General Dentistry'),
    ('Dr. Anoma Wickramasinghe', 'Oral Surgery')
) AS v(name, specialization)
WHERE NOT EXISTS (SELECT 1 FROM dentists);

INSERT INTO treatments (name, category, base_cost)
SELECT * FROM (VALUES
    ('Dental Cleaning', 'STANDARD', 4500.00),
    ('Cavity Filling', 'STANDARD', 8500.00),
    ('Root Canal', 'SURGICAL', 35000.00),
    ('Tooth Extraction', 'SURGICAL', 12000.00)
) AS v(name, category, base_cost)
WHERE NOT EXISTS (SELECT 1 FROM treatments);

INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status)
SELECT * FROM (VALUES
    ('APT-000001', 1, 1, 1, CURRENT_DATE, TIME '09:30:00', 'SCHEDULED'),
    ('APT-000002', 2, 2, 3, CURRENT_DATE, TIME '11:00:00', 'SCHEDULED')
) AS v(appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status)
WHERE NOT EXISTS (SELECT 1 FROM appointments);
