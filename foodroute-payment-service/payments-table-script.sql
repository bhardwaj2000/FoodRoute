CREATE TABLE IF NOT EXISTS payments (
    payment_id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50),
    created_at DATETIME,
    PRIMARY KEY (payment_id)
);