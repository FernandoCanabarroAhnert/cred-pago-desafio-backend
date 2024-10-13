INSERT INTO roles (authority) VALUES ('ROLE_ADMIN');
INSERT INTO roles (authority) VALUES ('ROLE_USER');

INSERT INTO users (full_name, email, password, activated) VALUES ('Maria Brown', 'maria@gmail.com', '$2a$10$EJbn6diYiYrEPTyDhTmZYugVgT9LhUPNk1NpcFm4FTBQg0BWK3COa', TRUE);
INSERT INTO users (full_name, email, password, activated) VALUES ('Alex Green', 'alex@gmail.com', '$2a$10$EJbn6diYiYrEPTyDhTmZYugVgT9LhUPNk1NpcFm4FTBQg0BWK3COa', TRUE);

INSERT INTO user_role(user_id,role_id) VALUES (1,2);
INSERT INTO user_role(user_id,role_id) VALUES (2,1);
INSERT INTO user_role(user_id,role_id) VALUES (2,2);

INSERT INTO carts (user_id) VALUES (1);
INSERT INTO carts (user_id) VALUES (2);

INSERT INTO credit_cards (holder_name,card_number,user_id,cvv,expiration_date) VALUES ('Maria Brown','1234123412341234',1,123,'12/25');
INSERT INTO credit_cards (holder_name,card_number,user_id,cvv,expiration_date) VALUES ('Alex Green','4321432143214321',2,321,'12/25');

INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('The Beatles', '1967', 'Sgt. Peppers Lonely Hearts Club Band', 150, 'sgt_pepper_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Pink Floyd', '1973', 'The Dark Side of the Moon', 120, 'dark_side_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Led Zeppelin', '1971', 'Led Zeppelin IV', 130, 'led_zeppelin_iv_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Michael Jackson', '1982', 'Thriller', 180, 'thriller_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Queen', '1975', 'A Night at the Opera', 140, 'night_at_the_opera_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Nirvana', '1991', 'Nevermind', 110, 'nevermind_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Radiohead', '1997', 'OK Computer', 160, 'ok_computer_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('The Rolling Stones', '1972', 'Exile on Main St.', 145, 'exile_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('David Bowie', '1972', 'The Rise and Fall of Ziggy Stardust', 125, 'ziggy_stardust_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('The Clash', '1979', 'London Calling', 135, 'london_calling_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Fleetwood Mac', '1977', 'Rumours', 150, 'rumours_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Bob Dylan', '1965', 'Highway 61 Revisited', 115, 'highway_61_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Prince', '1984', 'Purple Rain', 175, 'purple_rain_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('AC/DC', '1980', 'Back in Black', 140, 'back_in_black_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('U2', '1987', 'The Joshua Tree', 130, 'joshua_tree_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('The Who', '1971', 'Whos Next', 125, 'whos_next_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Bruce Springsteen', '1975', 'Born to Run', 120, 'born_to_run_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Eagles', '1976', 'Hotel California', 135, 'hotel_california_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('The Doors', '1967', 'The Doors', 110, 'the_doors_thumb.jpg');
INSERT INTO products (artist, release_year, album, price, thumb) VALUES ('Metallica', '1991', 'Metallica (The Black Album)', 150, 'black_album_thumb.jpg');

