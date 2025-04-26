CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       full_name VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(255),
                       role VARCHAR(255) CHECK (role IN ('HOST','GUEST','ADMIN'))
);

CREATE TABLE property (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          description VARCHAR(255) NOT NULL,
                          address VARCHAR(255) NOT NULL,
                          price_per_night NUMERIC(38,2) NOT NULL,
                          max_guests INTEGER NOT NULL,
                          average_rating DOUBLE PRECISION NOT NULL,
                          available BOOLEAN NOT NULL,
                          host_id BIGINT,
                          FOREIGN KEY (host_id) REFERENCES users(id)
);

CREATE TABLE image (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       image_url VARCHAR(255) NOT NULL,
                       property_id BIGINT,
                       FOREIGN KEY (property_id) REFERENCES property(id)
);

CREATE TABLE booking (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         check_in_date DATE NOT NULL,
                         check_out_date DATE NOT NULL,
                         guest_id BIGINT,
                         property_id BIGINT,
                         FOREIGN KEY (guest_id) REFERENCES users(id),
                         FOREIGN KEY (property_id) REFERENCES property(id),
                         status VARCHAR(255) CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','CHECKED_OUT'))
);

CREATE TABLE review (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        rating INTEGER NOT NULL,
                        comment VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        author_id BIGINT,
                        property_id BIGINT,
                        FOREIGN KEY (author_id) REFERENCES users(id),
                        FOREIGN KEY (property_id) REFERENCES property(id)
);