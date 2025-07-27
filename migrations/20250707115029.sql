-- admin_account
INSERT INTO admin_account (id, username, password, email, status, enable_mfa, mfa_secret, created_at, updated_at)
VALUES
    (1, 'admin1', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin1@example.com', 1, false, NULL, 1719990000000, 1719990000000),
    (2, 'admin2', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin2@example.com', 1, false, NULL, 1719990000001, 1719990000001),
    (3, 'admin3', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin3@example.com', 1, false, NULL, 1719990000002, 1719990000002),
    (4, 'admin4', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin4@example.com', 1, false, NULL, 1719990000003, 1719990000003),
    (5, 'admin5', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin5@example.com', 1, false, NULL, 1719990000004, 1719990000004),
    (6, 'admin6', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin6@example.com', 1, false, NULL, 1719990000005, 1719990000005),
    (7, 'admin7', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin7@example.com', 1, false, NULL, 1719990000006, 1719990000006),
    (8, 'admin8', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin8@example.com', 1, false, NULL, 1719990000007, 1719990000007),
    (9, 'admin9', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin9@example.com', 1, false, NULL, 1719990000008, 1719990000008),
    (10, 'admin10', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'admin10@example.com', 1, false, NULL, 1719990000009, 1719990000009);

-- user_account
INSERT INTO user_account (id, username, password, email, status, enable_mfa, mfa_secret, created_at, updated_at)
VALUES
    (1, 'user1', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user1@example.com', 1, false, NULL, 1719991000000, 1719991000000),
    (2, 'user2', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user2@example.com', 1, false, NULL, 1719991000001, 1719991000001),
    (3, 'user3', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user3@example.com', 1, false, NULL, 1719991000002, 1719991000002),
    (4, 'user4', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user4@example.com', 1, false, NULL, 1719991000003, 1719991000003),
    (5, 'user5', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user5@example.com', 1, false, NULL, 1719991000004, 1719991000004),
    (6, 'user6', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user6@example.com', 1, false, NULL, 1719991000005, 1719991000005),
    (7, 'user7', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user7@example.com', 1, false, NULL, 1719991000006, 1719991000006),
    (8, 'user8', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user8@example.com', 1, false, NULL, 1719991000007, 1719991000007),
    (9, 'user9', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user9@example.com', 1, false, NULL, 1719991000008, 1719991000008),
    (10, 'user10', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'user10@example.com', 1, false, NULL, 1719991000009, 1719991000009);

-- publisher
INSERT INTO publisher (id, name, email, phone, status, logo_url, created_at, updated_at)
VALUES
    (1, 'Publisher A', 'pub1@example.com', '0900000001', 1, NULL, 1719992000000, 1719992000000),
    (2, 'Publisher B', 'pub2@example.com', '0900000002', 1, NULL, 1719992000001, 1719992000001),
    (3, 'Publisher C', 'pub3@example.com', '0900000003', 1, NULL, 1719992000002, 1719992000002),
    (4, 'Publisher D', 'pub4@example.com', '0900000004', 1, NULL, 1719992000003, 1719992000003),
    (5, 'Publisher E', 'pub5@example.com', '0900000005', 1, NULL, 1719992000004, 1719992000004),
    (6, 'Publisher F', 'pub6@example.com', '0900000006', 1, NULL, 1719992000005, 1719992000005),
    (7, 'Publisher G', 'pub7@example.com', '0900000007', 1, NULL, 1719992000006, 1719992000006),
    (8, 'Publisher H', 'pub8@example.com', '0900000008', 1, NULL, 1719992000007, 1719992000007),
    (9, 'Publisher I', 'pub9@example.com', '0900000009', 1, NULL, 1719992000008, 1719992000008),
    (10, 'Publisher J', 'pub10@example.com', '0900000010', 1, NULL, 1719992000009, 1719992000009);

-- publisher_account
INSERT INTO publisher_account (id, publisher_id, username, password, email, status, enable_mfa, mfa_secret, created_at, updated_at)
VALUES
    (1, 1, 'pubacc1', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc1@example.com', 1, false, NULL, 1719993000000, 1719993000000),
    (2, 2, 'pubacc2', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc2@example.com', 1, false, NULL, 1719993000001, 1719993000001),
    (3, 3, 'pubacc3', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc3@example.com', 1, false, NULL, 1719993000002, 1719993000002),
    (4, 4, 'pubacc4', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc4@example.com', 1, false, NULL, 1719993000003, 1719993000003),
    (5, 5, 'pubacc5', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc5@example.com', 1, false, NULL, 1719993000004, 1719993000004),
    (6, 6, 'pubacc6', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc6@example.com', 1, false, NULL, 1719993000005, 1719993000005),
    (7, 7, 'pubacc7', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc7@example.com', 1, false, NULL, 1719993000006, 1719993000006),
    (8, 8, 'pubacc8', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc8@example.com', 1, false, NULL, 1719993000007, 1719993000007),
    (9, 9, 'pubacc9', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc9@example.com', 1, false, NULL, 1719993000008, 1719993000008),
    (10, 10, 'pubacc10', '10$0EPP0z1kqSBZ/0xwf.y3e.VNW.ruwdrj1c7I/95OZ1vZH9b1eF3j2', 'pubacc10@example.com', 1, false, NULL, 1719993000009, 1719993000009);


-- user_oauth2_account
INSERT INTO user_oauth2_account (id, oauth2_id, oauth2_provider, status, user_account_id)
VALUES
    (1, 'oauth2id1', 'google', 1, 1),
    (2, 'oauth2id2', 'facebook', 1, 2),
    (3, 'oauth2id3', 'google', 1, 3),
    (4, 'oauth2id4', 'facebook', 1, 4),
    (5, 'oauth2id5', 'google', 1, 5),
    (6, 'oauth2id6', 'facebook', 1, 6),
    (7, 'oauth2id7', 'google', 1, 7),
    (8, 'oauth2id8', 'facebook', 1, 8),
    (9, 'oauth2id9', 'google', 1, 9),
    (10, 'oauth2id10', 'facebook', 1, 10);