CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(255),
    restaurant_id VARCHAR(255),
    status VARCHAR(50),
    total_amount DECIMAL(19, 2),
    created_at DATETIME,
    PRIMARY KEY (order_id)
);