INSERT INTO users (email, password, full_name, phone_number, status, status_description, role) VALUES
    ('alice@example.com', '$2a$10$aHMzEfMH0GUuju4A09lCNO9sVBlaqUgOVCX3V0U8vE/30g.Ryl0VO', 'Alice Johnson', '+1234567890', 'ACTIVE', NULL, 'GUEST'),
    ('frank@example.com', '$2a$10$aHMzEfMH0GUuju4A09lCNO9sVBlaqUgOVCX3V0U8vE/30g.Ryl0VO', 'Frank White', '+1555666777', 'ACTIVE', NULL, 'GUEST'),
    ('bob@example.com', '$2a$10$aHMzEfMH0GUuju4A09lCNO9sVBlaqUgOVCX3V0U8vE/30g.Ryl0VO', 'Bob Smith', '+1987654321', 'WARNED', 'Suspicious activity detected', 'GUEST'),
    ('charlie@example.com', '$2a$10$aHMzEfMH0GUuju4A09lCNO9sVBlaqUgOVCX3V0U8vE/30g.Ryl0VO', 'Charlie Brown', '+1122334455', 'ACTIVE', NULL, 'HOST'),
    ('dave@example.com', '$2a$10$aHMzEfMH0GUuju4A09lCNO9sVBlaqUgOVCX3V0U8vE/30g.Ryl0VO', 'Dave Wilson', '+1222333444', 'WARNED', 'Violation of community guidelines', 'HOST'),
    ('eve@example.com', '$2a$10$aHMzEfMH0GUuju4A09lCNO9sVBlaqUgOVCX3V0U8vE/30g.Ryl0VO', 'Eve Adams', '+1444555666', 'ACTIVE', NULL, 'ADMIN');

INSERT INTO property (id, title, description, city, property_type, floor_area, bedrooms, address, price_per_night, max_guests, available, average_rating, host_id) VALUES
    (1, 'Modern Apartment in Warsaw', 'Spacious and sunny apartment in the heart of Warsaw.', 'Warsaw', 'APARTMENT', 80, 2, '123 Warsaw St', 150.00, 4, true, 4.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (2, 'Cozy Cabin in Krakow', 'Charming wooden cabin near the woods.', 'Krakow', 'CABIN', 60, 1, '45 Krakow Rd', 100.00, 2, true, 4.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (3, 'Luxury Villa in Gdansk', 'Beautiful villa with a private pool and garden.', 'Gdansk', 'VILLA', 200, 5, '78 Gdansk Ln', 350.00, 10, true, 4.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (4, 'City Loft in Poznan', 'Stylish loft with a modern touch.', 'Poznan', 'LOFT', 100, 3, '89 Poznan Ave', 200.00, 5, true, 0.0,
        (SELECT id FROM users WHERE email = 'dave@example.com')),
    (5, 'Countryside House in Warsaw', 'Peaceful house in a quiet area.', 'Warsaw', 'HOUSE', 120, 3, '23 Warsaw Hill', 120.00, 6, true, 0.0,
        (SELECT id FROM users WHERE email = 'dave@example.com')),
    (6, 'Studio Apartment in Wroclaw', 'Compact studio perfect for solo travelers.', 'Wroclaw', 'STUDIO', 35, 1, '56 Wroclaw Blvd', 75.00, 1, true, 0.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (7, 'Penthouse in Krakow', 'Luxurious penthouse with panoramic views.', 'Krakow', 'APARTMENT', 150, 4, '99 Krakow Skyline', 300.00, 8, true, 0.0,
        (SELECT id FROM users WHERE email = 'dave@example.com')),
    (8, 'Seaside Apartment in Gdansk', 'Beachfront apartment with modern amenities.', 'Gdansk', 'APARTMENT', 90, 2, '12 Gdansk Beach', 220.00, 4, true, 0.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (9, 'Cottage in Poznan', 'Charming countryside cottage.', 'Poznan', 'HOUSE', 80, 2, '67 Poznan Hills', 110.00, 3, true, 0.0,
        (SELECT id FROM users WHERE email = 'dave@example.com')),
    (10, 'Luxury Condo in Warsaw', 'Stylish condo in the city center.', 'Warsaw', 'APARTMENT', 100, 2, '101 Warsaw Central', 180.00, 4, true, 0.0,
        (SELECT id FROM users WHERE email = 'dave@example.com')),
    (11, 'Townhouse in Gdansk', 'Modern townhouse with spacious living area.', 'Gdansk', 'TOWNHOUSE', 130, 3, '111 Gdansk District', 250.00, 5, true, 0.0,
        (SELECT id FROM users WHERE email = 'dave@example.com')),
    (12, 'Farmhouse in Krakow', 'Rustic farmhouse surrounded by nature.', 'Krakow', 'FARMHOUSE', 160, 4, '23 Krakow Farm Rd', 200.00, 6, true, 0.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (13, 'Bungalow in Wroclaw', 'Comfortable bungalow with a garden view.', 'Wroclaw', 'BUNGALOW', 75, 2, '76 Wroclaw Gardens', 120.00, 3, true, 0.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (14, 'Loft Apartment in Warsaw', 'Contemporary loft in the heart of the city.', 'Warsaw', 'LOFT', 110, 2, '35 Warsaw Loft District', 190.00, 4, true, 0.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (15, 'Family House in Poznan', 'Spacious family house with a large yard.', 'Poznan', 'HOUSE', 150, 4, '88 Poznan Hillside', 160.00, 6, true, 0.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com')),
    (16, 'Studio Loft in Gdansk', 'Compact and modern studio loft.', 'Gdansk', 'STUDIO', 40, 1, '65 Gdansk City Center', 90.00, 2, true, 0.0,
        (SELECT id FROM users WHERE email = 'charlie@example.com'));

INSERT INTO booking (id, check_in_date, check_out_date, guest_id, property_id, status) VALUES
    (1, '2024-05-10', '2024-05-15', (SELECT id FROM users WHERE email = 'alice@example.com'), 1, 'CHECKED_OUT'),
    (2, '2024-06-01', '2024-06-05', (SELECT id FROM users WHERE email = 'frank@example.com'), 2, 'CHECKED_OUT'),
    (3, '2024-07-20', '2024-07-25', (SELECT id FROM users WHERE email = 'alice@example.com'), 3, 'CANCELLED'),
    (4, '2023-05-15', '2023-05-20', (SELECT id FROM users WHERE email = 'bob@example.com'), 1, 'CHECKED_OUT'),
    (5, '2023-06-10', '2023-06-15', (SELECT id FROM users WHERE email = 'frank@example.com'), 2, 'CANCELLED'),
    (6, '2023-07-01', '2023-07-07', (SELECT id FROM users WHERE email = 'alice@example.com'), 3, 'CHECKED_OUT'),
    (7, '2026-05-10', '2026-05-15', (SELECT id FROM users WHERE email = 'alice@example.com'), 1, 'PENDING'),
    (8, '2026-06-20', '2026-06-25', (SELECT id FROM users WHERE email = 'frank@example.com'), 2, 'CONFIRMED'),
    (9, '2026-07-15', '2026-07-20', (SELECT id FROM users WHERE email = 'bob@example.com'), 3, 'CONFIRMED'),
    (10, '2025-05-01', '2025-05-05', (SELECT id FROM users WHERE email = 'alice@example.com'), 2, 'CHECKED_OUT');

INSERT INTO review (id, rating, comment, created_at, author_id, property_id) VALUES
    (1, 5, 'Amazing place! Highly recommend.', '2024-05-16 10:00:00', (SELECT id FROM users WHERE email = 'alice@example.com'), 1),
    (2, 4, 'Great location but a bit noisy at night.', '2024-06-06 12:00:00', (SELECT id FROM users WHERE email = 'frank@example.com'), 2),
    (3, 3, 'Not as expected but still okay.', '2023-05-21 16:00:00', (SELECT id FROM users WHERE email = 'bob@example.com'), 1),
    (4, 4, 'Nice place but needs some maintenance.', '2023-07-08 20:00:00', (SELECT id FROM users WHERE email = 'alice@example.com'), 3);

INSERT INTO image (id, image_url, property_id) VALUES
    (1, 'https://bookinline-bucket.s3.eu-north-1.amazonaws.com/apart1.jpeg', 1),
    (2, 'https://bookinline-bucket.s3.eu-north-1.amazonaws.com/apart2.jpg', 1),
    (3, 'https://bookinline-bucket.s3.eu-north-1.amazonaws.com/cabin1.jpeg', 2),
    (4, 'https://bookinline-bucket.s3.eu-north-1.amazonaws.com/cabin2.jpg', 2),
    (5, 'https://bookinline-bucket.s3.eu-north-1.amazonaws.com/villa1.jpg', 3),
    (6, 'https://bookinline-bucket.s3.eu-north-1.amazonaws.com/villa2.jpg', 3),
    (7, 'https://bookinline-bucket.s3.eu-north-1.amazonaws.com/loft1.jpg', 4);

SELECT setval('booking_seq', (SELECT MAX(id) FROM booking));
SELECT setval('image_seq', (SELECT MAX(id) FROM image));
SELECT setval('property_seq', (SELECT MAX(id) FROM property));
SELECT setval('review_seq', (SELECT MAX(id) FROM review));