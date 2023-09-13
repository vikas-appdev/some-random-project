###############################################
####                                       ####
#### AUTHOR: VIKAS KUMAR                   ####
#### LICENCE: GRADLIC SOLUTIONS PVT LTD    ####
#### DATE: 06/SEPT/2023                    ####
#### VERSION: 1.0.0                        ####
####                                       ####
###############################################

/*
 * --- General Rules ---
 * Use underscore_names instead of CamelCase
 * Table name should be plural
 * Spell out id fields(item_id instead id)
 * Don't use ambiguous column names
 * Name foreign key columns the same as the columns they refer to
 * Use CAPS for all sql queries
 *
 */

CREATE SCHEMA IF NOT EXISTS all_in_one;

# SET NAMES 'UTF8MB4';
# SET TIME_ZONE = 'Us/Eastern';
# SET TIME_ZONE = '+5:30';


USE all_in_one;

DROP TABLE IF EXISTS users;

CREATE TABLE users(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    user_name VARCHAR(255),

    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) DEFAULT NULL,
    door_number VARCHAR(255),
    street VARCHAR(255),
    locality VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    pincode int,
    latitude double,
    longitude double,
    mobile_number VARCHAR(10),
    aadhaar_number VARCHAR(50),
    title VARCHAR(255),
    bio VARCHAR(255),
    is_active BOOLEAN DEFAULT FALSE,
    is_not_locked BOOLEAN DEFAULT TRUE,
    using_mfa BOOLEAN DEFAULT FALSE,
    profile_image_url VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    user_nfc_card_number VARCHAR(255),
    last_login_date DATETIME,
    last_login_date_display DATETIME,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT UQ_USERS_EMAIL UNIQUE(email)
);

DROP TABLE IF EXISTS roles;

CREATE TABLE roles(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    permission VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_ROLES_NAME UNIQUE(name)
);

DROP TABLE IF EXISTS user_roles;

CREATE TABLE user_roles(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT UQ_USER_ROLE_USER_ID UNIQUE(user_id)
);

DROP TABLE IF EXISTS events;

CREATE TABLE events(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL CHECK(type IN('LOGIN_ATTEMPT', 'LOGIN_ATTEMPT_FAILURE', 'LOGIN_ATTEMPT_SUCCESS', 'PROFILE_UPDATE', 'PROFILE_PICTURE_UPDATE', 'ROLE_UPDATE', 'ACCOUNT_SETTINGS_UPDATE', 'PASSWORD_UPDATE', 'MFA_UPDATE')),
    description VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_EVENTS_TYPE UNIQUE(type)
);

DROP TABLE IF EXISTS user_events;

CREATE TABLE user_events(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    event_id BIGINT UNSIGNED NOT NULL,
    device VARCHAR(100) DEFAULT NULL,
    ip_address VARCHAR(100) DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

DROP TABLE IF EXISTS account_verifications;

CREATE TABLE account_verifications(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    url VARCHAR(255) NOT NULL,
    -- date DATETIME NOT NULL
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_ACCOUNT_VERIFICATION_USER_ID UNIQUE(user_id),
    CONSTRAINT UQ_ACCOUNT_VERIFICATION_URL UNIQUE(url)
);

DROP TABLE IF EXISTS reset_password_verifications;

CREATE TABLE reset_password_verifications(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    url VARCHAR(255) NOT NULL,
    expiration_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_RESET_PASSWORD_VERIFICATION_USER_ID UNIQUE(user_id),
    CONSTRAINT UQ_RESET_PASSWORD_VERIFICATION_URL UNIQUE(url)
);

DROP TABLE IF EXISTS two_factor_verifications;

CREATE TABLE two_factor_verifications(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    code VARCHAR(10) NOT NULL,
    expiration_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_TWO_FACTOR_VERIFICATION_USER_ID UNIQUE(user_id),
    CONSTRAINT UQ_TWO_FACTOR_PASSWORD_VERIFICATION_CODE UNIQUE(code)
);




INSERT INTO roles(name, permission) VALUES('ROLE_USER', 'READ:USER,READ:CUSTOMER'),
('ROLE_MANAGER', 'READ:USER,READ:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER'),
('ROLE_ADMIN', 'READ:USER,READ:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER,CREATE:USER,CREATE:CUSTOMER'),
('ROLE_SYSADMIN', 'READ:USER,READ:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER,CREATE:USER,CREATE:CUSTOMER,DELETE:USER,DELETE:CUSTOMER');
