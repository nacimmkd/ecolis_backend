TRUNCATE TABLE reviews, bookings, parcels, trips, profiles, images, users
    RESTART IDENTITY CASCADE;

INSERT INTO users (id, email, password, role, is_verified, deleted, deleted_at)
    VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a1', 'nacim@gmail.com', '$2y$10$/2LiHT..yzR5EZaeGA1SsOoSeQswG4Mkl695RnEyNowG98twPiafS', 'USER', false, false, null);

INSERT INTO profiles (id, first_name, last_name, phone)
    VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a1', 'nacim', 'makedhi', '+33758328748');

INSERT INTO users (id, email, password, role, is_verified, deleted, deleted_at)
VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a2', 'mounir@gmail.com', '$2y$10$/2LiHT..yzR5EZaeGA1SsOoSeQswG4Mkl695RnEyNowG98twPiafS', 'USER', false, false, null);

INSERT INTO profiles (id, first_name, last_name, phone)
VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a2', 'mounir', 'khiyati', '+33758328750');


INSERT INTO users (id, email, password, role, is_verified, deleted, deleted_at)
VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a3', 'admin@gmail.com', '$2y$10$lx2xbuOz3c.1J/SBGiXgbudTLdHUCgBgHv2eX.mgxAsSNMFnDayXO', 'ADMIN', true, false, null);

INSERT INTO profiles (id, first_name, last_name, phone)
VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a3', 'admin', 'admin', '+33758328748');



INSERT INTO trips (
    id,
    user_id,

    departure_street,
    departure_city,
    departure_postal_code,
    departure_country,
    departure_lat,
    departure_lng,

    arrival_street,
    arrival_city,
    arrival_postal_code,
    arrival_country,
    arrival_lat,
    arrival_lng,

    departure_date,
    arrival_date,

    available_weight_kg,

    price_per_kg,
    max_detour_km,
    status,
    instant_booking,
    notes
)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'f76d4823-bf6c-4f9d-8529-4458686f55a1',

           '10 Rue de Rivoli',
           'Paris',
           '75001',
           'France',
           48.8566,
           2.3522,

           '20 Canebière',
           'Marseille',
           '13001',
           'France',
           43.2965,
           5.3698,

           CURRENT_DATE + INTERVAL '2 day',
           CURRENT_DATE + INTERVAL '3 day',

           20.00,
           15.00,

           5.00,
           'PUBLISHED',
           true,
           'Trip Paris -> Marseille'
       );

-- =========================
-- TRIP STOP
-- =========================

INSERT INTO trip_stops (
    id,
    trip_id,
    stop_order,
    street,
    city,
    postal_code,
    country,
    latitude,
    longitude
)
VALUES (
           '22222222-2222-2222-2222-222222222222',
           '11111111-1111-1111-1111-111111111111',
           1,
           'Place Bellecour',
           'Lyon',
           '69002',
           'France',
           45.7578,
           4.8320
       );

-- =========================
-- PARCEL
-- =========================

INSERT INTO parcels (
    id,
    user_id,
    description,
    weight_kg,
    size,
    is_fragile,

    pickup_street,
    pickup_city,
    pickup_postal_code,
    pickup_country,
    pickup_lat,
    pickup_lng,

    dropoff_street,
    dropoff_city,
    dropoff_postal_code,
    dropoff_country,
    dropoff_lat,
    dropoff_lng,

    status
)
VALUES (
           '33333333-3333-3333-3333-333333333333',
           'f76d4823-bf6c-4f9d-8529-4458686f55a2',
           'MacBook Pro 16',
           3.50,
           'M',
           true,

           '5 Avenue Anatole France',
           'Paris',
           '75007',
           'France',
           48.8584,
           2.2945,

           '8 Rue Paradis',
           'Marseille',
           '13001',
           'France',
           43.2965,
           5.3698,

           'PUBLISHED'
       );

-- =========================
-- BOOKING
-- =========================

INSERT INTO bookings (
    id,
    trip_id,
    parcel_id,
    status,
    price,
    pickup_code,
    dropoff_code,
    created_at,
    paid_at
)
VALUES (
           '55555555-5555-5555-5555-555555555555',
           '11111111-1111-1111-1111-111111111111',
           '33333333-3333-3333-3333-333333333333',
           'PAID',
           19.25,
           'PICK123',
           'DROP456',
           NOW(),
           NOW()
       );