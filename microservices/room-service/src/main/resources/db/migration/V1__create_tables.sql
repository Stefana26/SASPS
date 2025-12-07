-- Create hotels table
CREATE TABLE IF NOT EXISTS hotels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    phone_number VARCHAR(20),
    email VARCHAR(150),
    website VARCHAR(200),
    star_rating INTEGER,
    amenities VARCHAR(500),
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_number VARCHAR(10) NOT NULL,
    room_type VARCHAR(30) NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    max_occupancy INTEGER NOT NULL,
    description VARCHAR(1000),
    facilities VARCHAR(500),
    floor_number INTEGER,
    image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    CONSTRAINT uk_room_number_hotel UNIQUE (room_number, hotel_id),
    CONSTRAINT chk_room_type CHECK (room_type IN ('SINGLE', 'DOUBLE', 'TWIN', 'SUITE', 'DELUXE', 'PRESIDENTIAL')),
    CONSTRAINT chk_room_status CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'RESERVED', 'OUT_OF_SERVICE'))
);

-- Create indexes
CREATE INDEX idx_hotels_city ON hotels(city);
CREATE INDEX idx_hotels_country ON hotels(country);
CREATE INDEX idx_hotels_active ON hotels(active);
CREATE INDEX idx_rooms_hotel_id ON rooms(hotel_id);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_rooms_room_type ON rooms(room_type);

-- Insert sample data
INSERT INTO hotels (name, description, address, city, country, postal_code, phone_number, email, star_rating, amenities, active)
VALUES 
('Grand Hotel Bucharest', 'Luxury hotel in the heart of Bucharest', 'Calea Victoriei 123', 'Bucharest', 'Romania', '010101', '+40211234567', 'contact@grandhotel.ro', 5, 'WiFi,Pool,Spa,Restaurant,Bar,Gym', TRUE),
('Palace Hotel Cluj', 'Historic hotel in Cluj-Napoca', 'Str. Horea 45', 'Cluj-Napoca', 'Romania', '400174', '+40264123456', 'info@palacehotel.ro', 4, 'WiFi,Restaurant,Bar,Conference Room', TRUE);

-- Insert sample rooms
INSERT INTO rooms (hotel_id, room_number, room_type, price_per_night, max_occupancy, description, facilities, floor_number, status)
VALUES 
(1, '101', 'SINGLE', 150.00, 1, 'Cozy single room', 'TV,WiFi,MiniBar', 1, 'AVAILABLE'),
(1, '102', 'DOUBLE', 250.00, 2, 'Comfortable double room', 'TV,WiFi,MiniBar,Safe', 1, 'AVAILABLE'),
(1, '201', 'SUITE', 500.00, 4, 'Luxurious suite with city view', 'TV,WiFi,MiniBar,Safe,Jacuzzi', 2, 'AVAILABLE'),
(2, '101', 'SINGLE', 120.00, 1, 'Single room with modern amenities', 'TV,WiFi', 1, 'AVAILABLE'),
(2, '102', 'DOUBLE', 200.00, 2, 'Double room with balcony', 'TV,WiFi,MiniBar', 1, 'AVAILABLE');

