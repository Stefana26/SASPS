INSERT INTO hotels (name, description, address, city, country, postal_code, phone_number, email, website, star_rating, amenities, active, created_at, updated_at)
VALUES 
('Grand Hotel Bucharest', 'Luxury hotel in the heart of the capital', '123 Victory Street', 'Bucharest', 'Romania', '010101', '+40211234567', 'contact@grandhotel.ro', 'www.grandhotel.ro', 5, 'WiFi, Pool, SPA, Restaurant, Parking', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Central Hotel Cluj', 'Modern hotel in the heart of Cluj', '45 Union Square', 'Cluj-Napoca', 'Romania', '400001', '+40264123456', 'reservations@hotelcluj.ro', 'www.hotelcluj.ro', 4, 'WiFi, Restaurant, Bar, Parking', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Seaside Resort Constanta', 'Resort by the sea', '200 Mamaia Boulevard', 'Constanta', 'Romania', '900001', '+40241987654', 'info@seasideresort.ro', 'www.seasideresort.ro', 5, 'WiFi, Private Beach, Pool, SPA, Restaurant, Bar', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO rooms (hotel_id, room_number, room_type, price_per_night, max_occupancy, description, facilities, floor_number, status, created_at, updated_at)
VALUES 
(1, '101', 'SINGLE', 200.00, 1, 'Elegant single room with city view', 'WiFi, TV, Air Conditioning, Mini Bar', 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '102', 'DOUBLE', 300.00, 2, 'Double room with balcony', 'WiFi, TV, Air Conditioning, Mini Bar, Balcony', 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '201', 'SUITE', 600.00, 4, 'Luxurious suite with separate living room', 'WiFi, TV, Air Conditioning, Mini Bar, Jacuzzi, Kitchen', 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '301', 'PRESIDENTIAL', 1200.00, 6, 'Presidential suite with panoramic view', 'WiFi, TV, Air Conditioning, Mini Bar, Jacuzzi, Kitchen, Butler Service', 3, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '101', 'SINGLE', 150.00, 1, 'Comfortable single room', 'WiFi, TV, Air Conditioning', 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '102', 'DOUBLE', 220.00, 2, 'Modern double room', 'WiFi, TV, Air Conditioning, Mini Bar', 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '103', 'TWIN', 210.00, 2, 'Twin room with two separate beds', 'WiFi, TV, Air Conditioning', 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '201', 'DELUXE', 350.00, 3, 'Deluxe room with premium facilities', 'WiFi, TV, Air Conditioning, Mini Bar, Coffee Machine', 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '101', 'DOUBLE', 400.00, 2, 'Room with sea view', 'WiFi, TV, Air Conditioning, Mini Bar, Balcony with sea view', 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '201', 'SUITE', 800.00, 4, 'Seaside suite', 'WiFi, TV, Air Conditioning, Mini Bar, Jacuzzi, Kitchen, Terrace', 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '301', 'PRESIDENTIAL', 1500.00, 6, 'Presidential suite with direct beach access', 'WiFi, TV, Air Conditioning, Mini Bar, Jacuzzi, Kitchen, Butler Service, Private Terrace', 3, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, enabled, email_verified, created_at, updated_at)
VALUES
('admin', 'admin@test.com', '$2a$10$BI7Y4z1di7P9TBW7cFrPOOVDc9x3xAysztMflbbgqV6e7iS/GGxm.', 'Admin', 'System', '+40721000001', 'ADMIN', true, true, CURRENT_TIMESTAMP - INTERVAL '6 months', CURRENT_TIMESTAMP),
('andrei.ciobanu', 'andrei.ciobanu@test.com', '$2a$10$BI7Y4z1di7P9TBW7cFrPOOVDc9x3xAysztMflbbgqV6e7iS/GGxm.', 'Andrei', 'Ciobanu', '+40721123456', 'CUSTOMER', true, true, CURRENT_TIMESTAMP - INTERVAL '4 months', CURRENT_TIMESTAMP),
('maria.pop', 'maria.pop@test.com', '$2a$10$BI7Y4z1di7P9TBW7cFrPOOVDc9x3xAysztMflbbgqV6e7iS/GGxm.', 'Maria', 'Pop', '+40721234567', 'CUSTOMER', true, true, CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP),
('alex.ionescu', 'alex.ionescu@test.com', '$2a$10$BI7Y4z1di7P9TBW7cFrPOOVDc9x3xAysztMflbbgqV6e7iS/GGxm.', 'Alexandru', 'Ionescu', '+40721345678', 'CUSTOMER', true, true, CURRENT_TIMESTAMP - INTERVAL '2 months', CURRENT_TIMESTAMP);

INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, special_requests, confirmation_number, payment_status, payment_method, paid_amount, created_at, updated_at)
VALUES 
(2, 3, CURRENT_DATE - INTERVAL '45 days', CURRENT_DATE - INTERVAL '42 days', 2, 1800.00, 'CONFIRMED', 'Late check-in after 10 PM', 'BK-2024-001', 'PAID', 'CREDIT_CARD', 1800.00, CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '42 days');

INSERT INTO payments (booking_id, amount, status, payment_method, transaction_id, payment_gateway, payment_date, description, created_at, updated_at)
VALUES 
(1, 1800.00, 'COMPLETED', 'CREDIT_CARD', 'TXN-001-2024', 'Stripe', CURRENT_DATE - INTERVAL '60 days', 'Payment for booking BK-2024-001', CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '60 days');

INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, special_requests, confirmation_number, payment_status, payment_method, paid_amount, created_at, updated_at)
VALUES 
(3, 9, CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '25 days', 2, 2000.00, 'CONFIRMED', 'Room with sea view, preferably high floor', 'BK-2024-002', 'PAID', 'PAYPAL', 2000.00, CURRENT_DATE - INTERVAL '40 days', CURRENT_DATE - INTERVAL '25 days');

INSERT INTO payments (booking_id, amount, status, payment_method, transaction_id, payment_gateway, payment_date, description, created_at, updated_at)
VALUES 
(2, 2000.00, 'COMPLETED', 'PAYPAL', 'TXN-002-2024', 'PayPal', CURRENT_DATE - INTERVAL '40 days', 'Payment for booking BK-2024-002', CURRENT_DATE - INTERVAL '40 days', CURRENT_DATE - INTERVAL '40 days');

INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, special_requests, confirmation_number, payment_status, payment_method, paid_amount, created_at, updated_at)
VALUES 
(4, 8, CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '3 days', 3, 1750.00, 'CONFIRMED', 'Extra bed for child, non-smoking room', 'BK-2024-003', 'PAID', 'CREDIT_CARD', 1750.00, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '2 days');

INSERT INTO payments (booking_id, amount, status, payment_method, transaction_id, payment_gateway, payment_date, description, created_at, updated_at)
VALUES 
(3, 1750.00, 'COMPLETED', 'CREDIT_CARD', 'TXN-003-2024', 'Stripe', CURRENT_DATE - INTERVAL '15 days', 'Payment for booking BK-2024-003', CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '15 days');

INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, special_requests, confirmation_number, payment_status, payment_method, paid_amount, created_at, updated_at)
VALUES 
(2, 11, CURRENT_DATE + INTERVAL '10 days', CURRENT_DATE + INTERVAL '17 days', 4, 10500.00, 'CONFIRMED', 'Anniversary celebration, champagne and flowers in room', 'BK-2024-004', 'PAID', 'CREDIT_CARD', 10500.00, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '5 days');

INSERT INTO payments (booking_id, amount, status, payment_method, transaction_id, payment_gateway, payment_date, description, created_at, updated_at)
VALUES 
(4, 10500.00, 'COMPLETED', 'CREDIT_CARD', 'TXN-004-2024', 'Stripe', CURRENT_DATE - INTERVAL '5 days', 'Payment for booking BK-2024-004', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '5 days');

INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, special_requests, confirmation_number, payment_status, payment_method, paid_amount, created_at, updated_at)
VALUES 
(3, 2, CURRENT_DATE + INTERVAL '20 days', CURRENT_DATE + INTERVAL '23 days', 2, 900.00, 'CONFIRMED', 'Quiet room away from elevator', 'BK-2024-005', 'PAID', 'CREDIT_CARD', 900.00, CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE - INTERVAL '3 days');

INSERT INTO payments (booking_id, amount, status, payment_method, transaction_id, payment_gateway, payment_date, description, created_at, updated_at)
VALUES 
(5, 900.00, 'COMPLETED', 'CREDIT_CARD', 'TXN-005-2024', 'Stripe', CURRENT_DATE - INTERVAL '3 days', 'Payment for booking BK-2024-005', CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE - INTERVAL '3 days');

INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, special_requests, confirmation_number, payment_status, payment_method, paid_amount, created_at, updated_at)
VALUES 
(4, 6, CURRENT_DATE + INTERVAL '30 days', CURRENT_DATE + INTERVAL '35 days', 2, 1100.00, 'PENDING', 'Early check-in if possible', 'BK-2024-006', 'PENDING', NULL, NULL, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE - INTERVAL '1 day');

INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, special_requests, confirmation_number, payment_status, payment_method, paid_amount, cancelled_at, cancellation_reason, created_at, updated_at)
VALUES 
(2, 5, CURRENT_DATE + INTERVAL '15 days', CURRENT_DATE + INTERVAL '18 days', 1, 450.00, 'CANCELLED', NULL, 'BK-2024-007', 'REFUNDED', 'CREDIT_CARD', 450.00, CURRENT_DATE - INTERVAL '2 days', 'Change in travel plans due to work commitment', CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '2 days');

INSERT INTO payments (booking_id, amount, status, payment_method, transaction_id, payment_gateway, payment_date, description, created_at, updated_at)
VALUES 
(7, 450.00, 'REFUNDED', 'CREDIT_CARD', 'TXN-007-2024', 'Stripe', CURRENT_DATE - INTERVAL '10 days', 'Refund for cancelled booking BK-2024-007', CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE - INTERVAL '2 days');

INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, special_requests, confirmation_number, payment_status, payment_method, paid_amount, created_at, updated_at)
VALUES 
(3, 7, CURRENT_DATE + INTERVAL '45 days', CURRENT_DATE + INTERVAL '50 days', 2, 1050.00, 'CONFIRMED', 'Business trip, need invoice for company', 'BK-2024-008', 'PAID', 'BANK_TRANSFER', 1050.00, CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE - INTERVAL '7 days');

INSERT INTO payments (booking_id, amount, status, payment_method, transaction_id, payment_gateway, payment_date, description, created_at, updated_at)
VALUES 
(8, 1050.00, 'COMPLETED', 'BANK_TRANSFER', 'TXN-008-2024', 'Bank Transfer', CURRENT_DATE - INTERVAL '7 days', 'Payment for booking BK-2024-008', CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE - INTERVAL '7 days');
