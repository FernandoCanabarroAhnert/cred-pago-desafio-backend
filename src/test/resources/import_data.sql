-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS users (
    activated BOOLEAN, 
    id BIGSERIAL,  
    email VARCHAR(255),
    full_name VARCHAR(255),
    password VARCHAR(255),
    PRIMARY KEY (id)
);

-- Tabela de Cartões de Crédito
CREATE TABLE IF NOT EXISTS credit_cards (
    cvv INT,
    id BIGSERIAL,
    user_id BIGINT,
    card_number VARCHAR(255),
    expiration_date VARCHAR(255),
    holder_name VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT FK_credit_cards_users FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de Produtos
CREATE TABLE IF NOT EXISTS products (
    price INT,
    id BIGSERIAL,
    album VARCHAR(255),
    artist VARCHAR(255),
    release_year VARCHAR(255),
    thumb VARCHAR(255),
    PRIMARY KEY (id)
);

-- Tabela de Funções
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL,
    authority VARCHAR(255),
    PRIMARY KEY (id)
);

-- Tabela de Carrinhos
CREATE TABLE IF NOT EXISTS carts (
    id BIGSERIAL,
    user_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT FK_carts_users FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de Transações
CREATE TABLE IF NOT EXISTS transactions (
    total_to_pay INT,
    credit_card_id BIGINT,
    id BIGSERIAL,
    moment TIMESTAMP(6),
    user_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT FK_transactions_credit_card FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id),
    CONSTRAINT FK_transactions_users FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de Itens no Carrinho
CREATE TABLE IF NOT EXISTS product_cart_items (
    price INT,
    quantity INT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    transaction_id BIGINT,
    PRIMARY KEY (cart_id, product_id),
    CONSTRAINT FK_product_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT FK_product_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT FK_product_cart_items_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);




-- Tabela de Códigos de Ativação
CREATE TABLE IF NOT EXISTS activation_codes (
    validated BOOLEAN,
    created_at TIMESTAMP(6),
    expires_at TIMESTAMP(6),
    id BIGSERIAL,
    user_id BIGINT,
    validated_at TIMESTAMP(6),
    code VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT FK_activation_codes_users FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de Usuários e Funções
CREATE TABLE IF NOT EXISTS user_role (
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, user_id),
    CONSTRAINT FK_user_role_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT FK_user_role_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Inserção de Funções
INSERT INTO roles (authority) VALUES ('ROLE_ADMIN');
INSERT INTO roles (authority) VALUES ('ROLE_USER');

-- Inserção de Usuários
INSERT INTO users (full_name, email, password, activated) 
VALUES ('Maria Brown', 'maria@gmail.com', '$2a$10$EJbn6diYiYrEPTyDhTmZYugVgT9LhUPNk1NpcFm4FTBQg0BWK3COa', true);

INSERT INTO users (full_name, email, password, activated) 
VALUES ('Alex Green', 'alex@gmail.com', '$2a$10$EJbn6diYiYrEPTyDhTmZYugVgT9LhUPNk1NpcFm4FTBQg0BWK3COa', true);

-- Inserção de Funções dos Usuários
INSERT INTO user_role(user_id, role_id) VALUES (1, 2);
INSERT INTO user_role(user_id, role_id) VALUES (2, 1);
INSERT INTO user_role(user_id, role_id) VALUES (2, 2);

-- Inserção de Carrinhos
INSERT INTO carts (user_id) VALUES (1);
INSERT INTO carts (user_id) VALUES (2);

-- Inserção de Cartões de Crédito
INSERT INTO credit_cards (holder_name, card_number, user_id, cvv, expiration_date) 
VALUES ('Maria Brown', '1234123412341234', 1, 123, '12/2025');

INSERT INTO credit_cards (holder_name, card_number, user_id, cvv, expiration_date) 
VALUES ('Alex Green', '4321432143214321', 2, 321, '12/2025');

-- Inserção de Produtos
INSERT INTO products (artist, release_year, album, price, thumb) 
VALUES ('The Beatles', '1967', 'Sgt. Peppers Lonely Hearts Club Band', 150, 'sgt_pepper_thumb.jpg');

INSERT INTO products (artist, release_year, album, price, thumb) 
VALUES ('Pink Floyd', '1973', 'The Dark Side of the Moon', 120, 'dark_side_thumb.jpg');

INSERT INTO products (artist, release_year, album, price, thumb) 
VALUES ('Led Zeppelin', '1971', 'Led Zeppelin IV', 130, 'led_zeppelin_iv_thumb.jpg');

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

-- Inserção de Itens no Carrinho
INSERT INTO product_cart_items (cart_id, product_id, quantity, price) 
VALUES (2, 1, 1, 150);
