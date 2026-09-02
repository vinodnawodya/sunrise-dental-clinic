-- Seed data for H2 (dev/sandbox fallback). Mirrors db/data-mysql.sql.
-- Staff login credentials are documented in README.md
-- (username: admin, password: Sunrise123!).
INSERT INTO users (username, password_hash, role) VALUES
    ('admin', '$2a$10$g1mM/rwTz8IiS8jaeRvW9.uyODa9vfu8qRzapYGUpEpEO30h4M1GO', 'STAFF');

INSERT INTO patients (name, address, contact_number) VALUES
    ('Alice Morgan', '12 Bute Street, Cardiff', '07700123456'),
    ('Ben Carter', '48 Cathays Terrace, Cardiff', '07700223344'),
    ('Chloe Davies', '9 Whitchurch Road, Cardiff', '07700998877');

INSERT INTO dentists (name, specialization) VALUES
    ('Dr. Sarah Lewis', 'General Dentistry'),
    ('Dr. Michael Owusu', 'Oral Surgery');

INSERT INTO treatments (name, category, base_cost) VALUES
    ('Dental Cleaning', 'STANDARD', 50.00),
    ('Cavity Filling', 'STANDARD', 120.00),
    ('Root Canal', 'SURGICAL', 450.00),
    ('Tooth Extraction', 'SURGICAL', 200.00);

INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status) VALUES
    ('APT-000001', 1, 1, 1, CURRENT_DATE, '09:30:00', 'SCHEDULED'),
    ('APT-000002', 2, 2, 3, CURRENT_DATE, '11:00:00', 'SCHEDULED');
