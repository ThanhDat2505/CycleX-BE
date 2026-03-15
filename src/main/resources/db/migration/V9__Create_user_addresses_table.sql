-- V9: Create user_addresses table for multiple addresses per user
CREATE TABLE IF NOT EXISTS user_addresses (
    address_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    label         VARCHAR(50)  NOT NULL DEFAULT 'Nhà riêng',
    province      VARCHAR(100) NOT NULL,
    district      VARCHAR(100) NOT NULL,
    ward          VARCHAR(100) NOT NULL,
    street_address VARCHAR(300) NOT NULL,
    full_address  VARCHAR(500) NOT NULL,
    receiver_name VARCHAR(150),
    receiver_phone VARCHAR(30),
    is_default    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_addresses_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);
