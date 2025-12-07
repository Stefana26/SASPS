-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(30) NOT NULL,
    transaction_id VARCHAR(100) UNIQUE,
    payment_gateway VARCHAR(50),
    payment_date TIMESTAMP,
    description VARCHAR(500),
    failure_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED', 'CANCELLED')),
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH', 'OTHER'))
);

-- Create indexes
CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);

-- Insert sample payments
INSERT INTO payments (booking_id, amount, status, payment_method, transaction_id, payment_gateway, payment_date, description)
VALUES 
(1, 450.00, 'COMPLETED', 'CREDIT_CARD', 'TXN-SAMPLE-001', 'Stripe', CURRENT_TIMESTAMP, 'Payment for booking BK-ABC12345'),
(2, 500.00, 'COMPLETED', 'CREDIT_CARD', 'TXN-SAMPLE-002', 'Stripe', CURRENT_TIMESTAMP, 'Payment for booking BK-DEF67890');

