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

-- genre
INSERT INTO genre (name, description, slug)
VALUES
    ( 'Action', 'Action games', 'action'),
    ( 'Adventure', 'Adventure games', 'adventure'),
    ( 'RPG', 'Role-playing games', 'rpg'),
    ( 'Strategy', 'Strategy games', 'strategy'),
    ( 'Simulation', 'Simulation games', 'simulation'),
    ( 'Sports', 'Sports games', 'sports'),
    ( 'Puzzle', 'Puzzle games', 'puzzle'),
    ( 'Racing', 'Racing games', 'racing'),
    ( 'Shooter', 'Shooter games', 'shooter'),
    ( 'Horror', 'Horror games', 'horror');

-- tag
INSERT INTO tag ( name, description, slug)
VALUES
    ( 'Multiplayer', 'Multiplayer mode', 'multiplayer'),
    ( 'Singleplayer', 'Singleplayer mode', 'singleplayer'),
    ( 'Co-op', 'Cooperative mode', 'coop'),
    ( 'Open World', 'Open world gameplay', 'open-world'),
    ( 'Story Rich', 'Rich story', 'story-rich'),
    ( 'Casual', 'Casual play', 'casual'),
    ( 'Difficult', 'Challenging', 'difficult'),
    ( 'Pixel Graphics', 'Pixel art', 'pixel-graphics'),
    ( 'VR', 'Virtual Reality', 'vr'),
    ( 'Fantasy', 'Fantasy theme', 'fantasy');

-- game
INSERT INTO game (id, publisher_id, name, price, status, release_date, created_at, updated_at)
VALUES
    (1, 1, 'Game A', 199000, 1, 1719000000000, 1719000000000, 1719000000000),
    (2, 2, 'Game B', 99000, 1, 1719000001000, 1719000001000, 1719000001000),
    (3, 3, 'Game C', 149000, 1, 1719000002000, 1719000002000, 1719000002000),
    (4, 4, 'Game D', 249000, 1, 1719000003000, 1719000003000, 1719000003000),
    (5, 5, 'Game E', 299000, 1, 1719000004000, 1719000004000, 1719000004000),
    (6, 6, 'Game F', 199000, 1, 1719000005000, 1719000005000, 1719000005000),
    (7, 7, 'Game G', 99000, 1, 1719000006000, 1719000006000, 1719000006000),
    (8, 8, 'Game H', 149000, 1, 1719000007000, 1719000007000, 1719000007000),
    (9, 9, 'Game I', 249000, 1, 1719000008000, 1719000008000, 1719000008000),
    (10, 10, 'Game J', 299000, 1, 1719000009000, 1719000009000, 1719000009000);

-- game_genre
INSERT INTO game_genre (game_id, genre_id)
VALUES
    (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10);

-- game_tag
INSERT INTO game_tag (game_id, tag_id)
VALUES
    (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10);

-- game_version
INSERT INTO game_version (id, game_id, name, change_log, exec_path, download_url, status, release_date, created_at, updated_at)
VALUES
    (1, 1, 'v1.0', 'Initial release', '/gameA/v1', 'http://example.com/gameA/v1', 1, 1719000000000, 1719000000000, 1719000000000),
    (2, 2, 'v1.0', 'Initial release', '/gameB/v1', 'http://example.com/gameB/v1', 1, 1719000001000, 1719000001000, 1719000001000),
    (3, 3, 'v1.0', 'Initial release', '/gameC/v1', 'http://example.com/gameC/v1', 1, 1719000002000, 1719000002000, 1719000002000),
    (4, 4, 'v1.0', 'Initial release', '/gameD/v1', 'http://example.com/gameD/v1', 1, 1719000003000, 1719000003000, 1719000003000),
    (5, 5, 'v1.0', 'Initial release', '/gameE/v1', 'http://example.com/gameE/v1', 1, 1719000004000, 1719000004000, 1719000004000),
    (6, 6, 'v1.0', 'Initial release', '/gameF/v1', 'http://example.com/gameF/v1', 1, 1719000005000, 1719000005000, 1719000005000),
    (7, 7, 'v1.0', 'Initial release', '/gameG/v1', 'http://example.com/gameG/v1', 1, 1719000006000, 1719000006000, 1719000006000),
    (8, 8, 'v1.0', 'Initial release', '/gameH/v1', 'http://example.com/gameH/v1', 1, 1719000007000, 1719000007000, 1719000007000),
    (9, 9, 'v1.0', 'Initial release', '/gameI/v1', 'http://example.com/gameI/v1', 1, 1719000008000, 1719000008000, 1719000008000),
    (10, 10, 'v1.0', 'Initial release', '/gameJ/v1', 'http://example.com/gameJ/v1', 1, 1719000009000, 1719000009000, 1719000009000);

-- user_game
INSERT INTO user_game (user_account_id, game_id, owned_date, play_seconds, play_recent_date)
VALUES
    (1, 1, 1719994000000, 3600, 1719994000000),
    (2, 2, 1719994000001, 7200, 1719994000001),
    (3, 3, 1719994000002, 1800, 1719994000002),
    (4, 4, 1719994000003, 5400, 1719994000003),
    (5, 5, 1719994000004, 1200, 1719994000004),
    (6, 6, 1719994000005, 600, 1719994000005),
    (7, 7, 1719994000006, 900, 1719994000006),
    (8, 8, 1719994000007, 300, 1719994000007),
    (9, 9, 1719994000008, 0, NULL),
    (10, 10, 1719994000009, 0, NULL);

-- cart
INSERT INTO cart (id, user_account_id, created_at, updated_at)
VALUES
    (1, 1, 1719995000000, 1719995000000),
    (2, 2, 1719995000001, 1719995000001),
    (3, 3, 1719995000002, 1719995000002),
    (4, 4, 1719995000003, 1719995000003),
    (5, 5, 1719995000004, 1719995000004),
    (6, 6, 1719995000005, 1719995000005),
    (7, 7, 1719995000006, 1719995000006),
    (8, 8, 1719995000007, 1719995000007),
    (9, 9, 1719995000008, 1719995000008),
    (10, 10, 1719995000009, 1719995000009);

-- cart_item
INSERT INTO cart_item (id, cart_id, game_id, added_at)
VALUES
    (1, 1, 2, 1719996000000),
    (2, 2, 3, 1719996000001),
    (3, 3, 4, 1719996000002),
    (4, 4, 5, 1719996000003),
    (5, 5, 6, 1719996000004),
    (6, 6, 7, 1719996000005),
    (7, 7, 8, 1719996000006),
    (8, 8, 9, 1719996000007),
    (9, 9, 10, 1719996000008),
    (10, 10, 1, 1719996000009);

-- orders
INSERT INTO orders (id, user_account_id, status, message, created_at, updated_at)
VALUES
    (1, 1, 1, 'Order 1', 1719997000000, 1719997000000),
    (2, 2, 1, 'Order 2', 1719997000001, 1719997000001),
    (3, 3, 1, 'Order 3', 1719997000002, 1719997000002),
    (4, 4, 1, 'Order 4', 1719997000003, 1719997000003),
    (5, 5, 1, 'Order 5', 1719997000004, 1719997000004),
    (6, 6, 1, 'Order 6', 1719997000005, 1719997000005),
    (7, 7, 1, 'Order 7', 1719997000006, 1719997000006),
    (8, 8, 1, 'Order 8', 1719997000007, 1719997000007),
    (9, 9, 1, 'Order 9', 1719997000008, 1719997000008),
    (10, 10, 1, 'Order 10', 1719997000009, 1719997000009);

-- order_details
INSERT INTO order_details (id, order_id, game_id, price)
VALUES
    (1, 1, 1, 199000),
    (2, 2, 2, 99000),
    (3, 3, 3, 149000),
    (4, 4, 4, 249000),
    (5, 5, 5, 299000),
    (6, 6, 6, 199000),
    (7, 7, 7, 99000),
    (8, 8, 8, 149000),
    (9, 9, 9, 249000),
    (10, 10, 10, 299000);

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