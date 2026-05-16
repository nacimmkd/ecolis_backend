TRUNCATE TABLE reviews, bookings, parcels, trips, profiles, images, users
    RESTART IDENTITY CASCADE;

INSERT INTO users (id, email, password, role, is_verified, deleted, deleted_at)
    VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a1', 'nacim@gmail.com', '$2y$10$/2LiHT..yzR5EZaeGA1SsOoSeQswG4Mkl695RnEyNowG98twPiafS', 'USER', false, false, null);

INSERT INTO profiles (id, first_name, last_name, phone)
    VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a1', 'nacim', 'makedhi', '+33758328748');

INSERT INTO users (id, email, password, role, is_verified, deleted, deleted_at)
VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a2', 'admin@gmail.com', '$2y$10$lx2xbuOz3c.1J/SBGiXgbudTLdHUCgBgHv2eX.mgxAsSNMFnDayXO', 'ADMIN', true, false, null);

INSERT INTO profiles (id, first_name, last_name, phone)
VALUES ('f76d4823-bf6c-4f9d-8529-4458686f55a2', 'admin', 'admin', '+33758328748');
