-- Create bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INTEGER NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    special_requests VARCHAR(1000),
    confirmation_number VARCHAR(50) UNIQUE,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    paid_amount DECIMAL(10, 2),
    cancelled_at TIMESTAMP,
    cancellation_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'NO_SHOW')),
    CONSTRAINT chk_payment_status CHECK (payment_status IN ('PENDING', 'PAID', 'PARTIALLY_PAID', 'REFUNDED', 'FAILED'))
);

-- Create indexes
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_room_id ON bookings(room_id);
CREATE INDEX idx_bookings_confirmation_number ON bookings(confirmation_number);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_check_in_date ON bookings(check_in_date);
CREATE INDEX idx_bookings_check_out_date ON bookings(check_out_date);

-- Insert sample bookings (user_id and room_id reference other services)
INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status, confirmation_number, payment_status, payment_method, paid_amount)
VALUES 
(2, 1, CURRENT_DATE + INTERVAL '7 days', CURRENT_DATE + INTERVAL '10 days', 1, 450.00, 'CONFIRMED', 'BK-ABC12345', 'PAID', 'CREDIT_CARD', 450.00),
(2, 2, CURRENT_DATE + INTERVAL '14 days', CURRENT_DATE + INTERVAL '16 days', 2, 500.00, 'CONFIRMED', 'BK-DEF67890', 'PAID', 'CREDIT_CARD', 500.00);

