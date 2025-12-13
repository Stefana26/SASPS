-- Create users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on username for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Create index on email for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, role, is_active)
VALUES (
    'admin',
    'admin@sasps.com',
    '$2a$10$XQYvZ8xVJYKxCkZyNkDO7OMnK3tC.YdWD6mF8X8xH9tKvLjpYb4Iq',
    'Admin',
    'User',
    'ADMIN',
    true
) ON CONFLICT (username) DO NOTHING;

-- Insert default test user (password: user123)
INSERT INTO users (username, email, password, first_name, last_name, role, is_active)
VALUES (
    'testuser',
    'test@sasps.com',
    '$2a$10$7pqHqZ0Y3KYhNR7HoWQdZO8E5YvMxJKYkxHjQZYjQxYjQxYjQxYjQ',
    'Test',
    'User',
    'USER',
    true
) ON CONFLICT (username) DO NOTHING;
