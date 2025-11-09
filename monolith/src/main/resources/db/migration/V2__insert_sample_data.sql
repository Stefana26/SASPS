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
