DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'steakdb') THEN
            CREATE DATABASE steakdb;
        END IF;
    END
$$;

-- ACCOUNT --

create table user_account
(
    id         bigint       not null primary key,
    username   varchar(32)  not null unique,
    password   varchar(255) not null,
    email      varchar(255) not null unique,
    status     smallint     not null default 0,
    enable_mfa bool         not null default false,
    mfa_secret varchar(255),
    created_at timestamp    not null default current_timestamp,
    updated_at timestamp             default current_timestamp
);

CREATE INDEX idx_username ON user_account (username);
CREATE INDEX idx_email ON user_account (username);

create table user_oauth2_account
(
    id              bigint       not null primary key,
    oauth2_id       varchar(255) not null,
    oauth2_provider varchar(255) not null,
    status          smallint     not null,
    user_account_id bigint       not null,
    foreign key (user_account_id) references user_account (id)
);

CREATE INDEX idx_oauth2_id_provider ON user_oauth2_account (oauth2_id, oauth2_provider);
CREATE INDEX idx_user_account_id ON user_oauth2_account (user_account_id);

create table user_refresh_token
(
    id          bigint                              not null primary key,
    account_id  bigint                              not null,
    device_id   varchar(64)                         not null,
    issues_at   timestamp default current_timestamp not null,
    expires_at  timestamp                           not null,
    revoked     boolean   default false             not null,
    token       varchar(64)                         not null unique,
    device_info varchar(255)                        not null,
    foreign key (account_id) references user_account (id)
);

CREATE INDEX idx_account_id ON user_refresh_token (account_id);
CREATE INDEX idx_token_device_id ON user_refresh_token (token, device_id);

-- PUBLISHER --

create table publisher
(
    id         bigint                              not null primary key,
    name       varchar(255)                        not null unique,
    email      varchar(255)                        not null unique,
    status     smallint                            not null,
    logo_url   varchar(255)                        not null,
    created_at timestamp default current_timestamp not null,
    updated_at timestamp default current_timestamp not null
);

create table publisher_role
(
    id           bigint                              not null primary key,
    publisher_id bigint,
    name         varchar(255)                        not null,
    active       bool      default true              not null,
    description  varchar(255),
    updated_at   timestamp default current_timestamp not null,
    foreign key (publisher_id) references publisher (id)
);

CREATE INDEX idx_publisher_role_publisher_id ON publisher_role (publisher_id);

create table publisher_permission_group
(
    id          int          not null primary key,
    name        varchar(255) not null,
    description varchar(255)
);

create table publisher_permission
(
    id          int         not null primary key,
    group_id    int         not null,
    name        varchar(64) not null unique,
    description varchar(255),
    authorities jsonb       not null,
    foreign key (group_id) references publisher_permission_group (id)
);

create table publisher_permission_role
(
    id                      bigint not null primary key,
    publisher_role_id       bigint not null,
    publisher_permission_id int    not null,
    foreign key (publisher_role_id) references publisher_role (id),
    foreign key (publisher_permission_id) references publisher_permission_role (id)
);

CREATE INDEX idx_role_id ON publisher_permission_role (publisher_role_id);
CREATE INDEX idx_permission_id ON publisher_permission_role (publisher_permission_id);
CREATE INDEX idx_permission_role ON publisher_permission_role (publisher_role_id, publisher_permission_id);

create table publisher_account
(
    id           bigint       not null primary key,
    publisher_id bigint       not null,
    username     varchar(32)  not null unique,
    password     varchar(255) not null,
    email        varchar(255) not null unique,
    status       smallint     not null default 0,
    enable_mfa   bool         not null default false,
    mfa_secret   varchar(255),
    created_at   timestamp    not null default current_timestamp,
    updated_at   timestamp             default current_timestamp,
    foreign key (publisher_id) references publisher (id)
);

CREATE INDEX idx_account_publisher ON publisher_account (publisher_id);
CREATE INDEX idx_publisher_username ON publisher_account (username);
CREATE INDEX idx_publisher_email ON publisher_account (email);

create table publisher_refresh_token
(
    id          bigint                              not null primary key,
    account_id  bigint                              not null,
    device_id   varchar(64)                         not null,
    issues_at   timestamp default current_timestamp not null,
    expires_at  timestamp                           not null,
    revoked     boolean   default false             not null,
    token       varchar(64)                         not null unique,
    device_info varchar(255)                        not null,
    foreign key (account_id) references publisher_account (id)
);
CREATE INDEX idx_publisher_account_id ON publisher_refresh_token (account_id);
CREATE INDEX idx_publisher_token_device_id ON publisher_refresh_token (token, device_id);

create table publisher_account_role
(
    id                   bigint not null primary key,
    publisher_account_id bigint not null,
    publisher_role_id    bigint not null,
    foreign key (publisher_account_id) references publisher_account (id),
    foreign key (publisher_role_id) references publisher_role (id)
);

CREATE INDEX idx_publisher_account_role_account_id ON publisher_account_role (publisher_account_id);
CREATE INDEX idx_publisher_account_role_role_id ON publisher_account_role (publisher_account_id);
CREATE INDEX idx_publisher_account_role_both ON publisher_account_role (publisher_account_id, publisher_role_id);

-- END PUBLISHER --

-- GAME --

create table game
(
    id           bigint         not null primary key,
    publisher_id bigint         not null,
    name         varchar(255)   not null,
    price        numeric(13, 2) not null,
    status       int            not null,
    release_date timestamp      not null,
    created_at   timestamp      not null,
    updated_at   timestamp      not null,
    foreign key (publisher_id) references publisher (id)
);

CREATE INDEX idx_game_publisher ON game (publisher_id);
CREATE INDEX idx_game_release_date ON game (release_date);
CREATE INDEX idx_game_name ON game (name);

create table game_version
(
    id           bigint       not null primary key,
    game_id      bigint       not null,
    name         varchar(255) not null,
    change_log   text,
    exec_path    varchar(255) not null,
    download_url varchar(255) not null,
    status       int          not null,
    release_date timestamp    not null,
    created_at   timestamp    not null default current_timestamp,
    updated_at   timestamp    not null default current_timestamp,
    foreign key (game_id) references game (id)
);

CREATE INDEX idx_version_game ON game_version (game_id);
CREATE INDEX idx_release_date ON game_version (game_id, release_date, status);

create table genre
(
    id          int          not null primary key,
    name        varchar(64)  not null,
    description text,
    slug        varchar(128) not null unique
);

CREATE INDEX idx_genre_slug ON genre (slug);

create table game_genre
(
    id       bigint not null primary key,
    game_id  bigint not null,
    genre_id bigint not null,
    foreign key (game_id) references game (id),
    foreign key (genre_id) references genre (id)
);

CREATE INDEX idx_genre_game ON game_genre (game_id);
CREATE INDEX idx_genre_genre ON game_genre (genre_id);
CREATE INDEX idx_genre_game_both ON game_genre (game_id, genre_id);

create table tag
(
    id          int          not null primary key,
    name        varchar(64)  not null,
    description text,
    slug        varchar(128) not null unique
);

CREATE INDEX idx_tag_slug ON tag (slug);

create table game_tag
(
    id      bigint not null primary key,
    game_id bigint not null,
    tag_id  bigint not null,
    foreign key (game_id) references game (id),
    foreign key (tag_id) references tag (id)
);

CREATE INDEX idx_game_tag_game_id ON game_tag (game_id);
CREATE INDEX idx_game_tag_tag_id ON game_tag (tag_id);
CREATE INDEX idx_game_tag_both ON game_tag (game_id, tag_id);


-- DEFAULT DATA
-- Master
INSERT INTO publisher_role values (0,null,'Master',true,'The king, can do everything and promote everyone');
INSERT INTO publisher_permission_group values (0,'Master','Can create or update games, exclude some sensitive data');
INSERT INTO publisher_permission values (0,0,'Master','Can do everything','["MASTER"]');
INSERT INTO publisher_permission_role values (0,0,0);
-- End

INSERT INTO publisher_permission_group values (1,'Game developer','Contains all of permissions about games');

