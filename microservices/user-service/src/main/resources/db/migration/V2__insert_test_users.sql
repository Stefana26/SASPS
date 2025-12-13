-- Insert test users (copied & adapted from monolith V2 sample data)
INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, enabled, email_verified, created_at, updated_at)
VALUES
('admin', 'admin@test.com', '$2a$10$BI7Y4z1di7P9TBW7cFrPOOVDc9x3xAysztMflbbgqV6e7iS/GGxm.', 'Admin', 'System', '+40721000001', 'ADMIN', true, true, CURRENT_TIMESTAMP - INTERVAL '6 months', CURRENT_TIMESTAMP),
('andrei.ciobanu', 'andrei.ciobanu@test.com', '$2a$10$BI7Y4z1di7P9TBW7cFrPOOVDc9x3xAysztMflbbgqV6e7iS/GGxm.', 'Andrei', 'Ciobanu', '+40721123456', 'CUSTOMER', true, true, CURRENT_TIMESTAMP - INTERVAL '4 months', CURRENT_TIMESTAMP),
('maria.pop', 'maria.pop@test.com', '$2a$10$BI7Y4z1di7P9TBW7cFrPOOVDc9x3xAysztMflbbgqV6e7iS/GGxm.', 'Maria', 'Pop', '+40721234567', 'CUSTOMER', true, true, CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP),
('alex.ionescu', 'alex.ionescu@test.com', '$2a$10$BI7Y4z1di7P9TBW7cFrPOOVDc9x3xAysztMflbbgqV6e7iS/GGxm.', 'Alexandru', 'Ionescu', '+40721345678', 'CUSTOMER', true, true, CURRENT_TIMESTAMP - INTERVAL '2 months', CURRENT_TIMESTAMP);
