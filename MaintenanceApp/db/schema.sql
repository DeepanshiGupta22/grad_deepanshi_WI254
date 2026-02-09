CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    phone_number VARCHAR(10) CHECK (LENGTH(phone_number) = 10 AND phone_number ~ '^[0-9]+$'),
    role VARCHAR(20) CHECK (role IN ('ADMIN', 'OWNER'))
);

CREATE TABLE sites (
    site_id SERIAL PRIMARY KEY,
    dimension VARCHAR(20),
    area_sqft INT,
    category VARCHAR(20) CHECK (category IN ('OPEN', 'CLOSED')),
    owner_id INT REFERENCES users(user_id)
);

CREATE TABLE maintenance (
    id SERIAL PRIMARY KEY,
    site_id INT REFERENCES sites(site_id),
    year INT,
    total_amount DECIMAL(10,2),
    paid_amount DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'UNPAID', -- UNPAID, PENDING (Partial), PAID
    UNIQUE(site_id, year) -- Prevents duplicate bills for the same year
);

CREATE TABLE site_update_requests (
    req_id SERIAL PRIMARY KEY,
    site_id INT REFERENCES sites(site_id),
    owner_id INT REFERENCES users(user_id),
    requested_category VARCHAR(20),
    status VARCHAR(20) DEFAULT 'PENDING' -- PENDING, APPROVED, REJECTED
);
INSERT INTO users (username, password, role, phone_number) VALUES 
('admin', 'admin123', 'ADMIN', '9876543210'),
('owner1', 'pass123', 'OWNER', '9000000001'),
('owner2', 'pass123', 'OWNER', '9000000002');

INSERT INTO sites (dimension, area_sqft, category, owner_id) VALUES 
('40x60', 2400, 'OPEN', 2),
('30x40', 1200, 'CLOSED', 2),
('30x50', 1500, 'OPEN', 3);