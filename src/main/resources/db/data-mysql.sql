-- Seed data for MySQL. Staff login credentials are documented in README.md.
-- Password hash below is BCrypt for the plaintext password documented there
-- (username: admin, password: Sunrise123!).
INSERT INTO users (username, password_hash, role) VALUES
    ('admin', '$2a$10$g1mM/rwTz8IiS8jaeRvW9.uyODa9vfu8qRzapYGUpEpEO30h4M1GO', 'STAFF');

INSERT INTO patients (name, address, contact_number) VALUES
    ('Kasun Perera', '45 Galle Road, Colombo 03', '0771234567'),
    ('Nimali Fernando', '12 Kandy Road, Kadawatha', '0712345678'),
    ('Chathura Bandara', '8 Temple Lane, Kandy', '0765432109');

INSERT INTO dentists (name, specialization) VALUES
    ('Dr. Priyantha Jayasuriya', 'General Dentistry'),
    ('Dr. Anoma Wickramasinghe', 'Oral Surgery');

INSERT INTO treatments (name, category, base_cost) VALUES
    ('Dental Cleaning', 'STANDARD', 4500.00),
    ('Cavity Filling', 'STANDARD', 8500.00),
    ('Root Canal', 'SURGICAL', 35000.00),
    ('Tooth Extraction', 'SURGICAL', 12000.00);

INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status) VALUES
    ('APT-000001', 1, 1, 1, CURDATE(), '09:30:00', 'SCHEDULED'),
    ('APT-000002', 2, 2, 3, CURDATE(), '11:00:00', 'SCHEDULED');
