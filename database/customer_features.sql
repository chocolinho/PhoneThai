-- Billing information table
CREATE TABLE IF NOT EXISTS billing_info (
    billing_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    card_name    VARCHAR(120) NOT NULL,
    card_number  VARCHAR(32) NOT NULL,
    bank_name    VARCHAR(120) NOT NULL,
    expiry_month TINYINT NOT NULL,
    expiry_year  SMALLINT NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_billing_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_billing_user UNIQUE (user_id)
);

-- Addresses table for users
CREATE TABLE IF NOT EXISTS user_addresses (
    address_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    full_name    VARCHAR(120) NOT NULL,
    phone        VARCHAR(32) NOT NULL,
    address_line VARCHAR(255) NOT NULL,
    ward         VARCHAR(120),
    district     VARCHAR(120),
    province     VARCHAR(120),
    is_default   TINYINT(1) DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
