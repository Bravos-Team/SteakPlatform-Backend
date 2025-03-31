DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'steakdb') THEN
            CREATE DATABASE steakdb;
        END IF;
    END
$$;

-- ACCOUNT --

create table account
(
    id         bigint       not null primary key,
    username   varchar(32)  not null unique,
    password   varchar(255) not null,
    email      varchar(255) not null unique,
    status     smallint     not null default 0,
    created_at timestamp             default current_timestamp not null,
    updated_at timestamp             default current_timestamp
);

create table account_refresh_token
(
    id         bigint                              not null primary key,
    account_id bigint                              not null,
    device_id  bigint                              not null,
    issues_at  timestamp default current_timestamp not null,
    expires_at timestamp                           not null,
    revoked    boolean   default false             not null,
    jti        bigint                              not null,
    foreign key (account_id) references account (id)
);

-- PUBLISHER --

create table publisher
(
    id          bigint       not null primary key,
    name        varchar(255) not null,
    email       varchar(255) not null unique,
    phone       varchar(16)  not null,
    avatar      varchar(255) not null,
    description text         null
);

create table publisher_role
(
    id           bigint       not null primary key,
    publisher_id bigint,
    name         varchar(255) not null,
    updated_at   timestamp default current_timestamp,
    foreign key (publisher_id) references publisher (id)
);

create table publisher_permission
(
    id          int         not null primary key,
    name        varchar(64) not null unique,
    description varchar(255)
);

create table publisher_role_permission
(
    id                      bigint not null primary key,
    publisher_role_id       bigint not null,
    publisher_permission_id int    not null,
    foreign key (publisher_role_id) references publisher_role (id),
    foreign key (publisher_permission_id) references publisher_permission (id)
);

create table publisher_account
(
    id           bigint       not null primary key,
    publisher_id bigint       not null,
    role_id      bigint,
    email        varchar(255) not null unique,
    password     varchar(255) not null,
    name         varchar(128) not null,
    status       smallint     not null default 0,
    created_at   timestamp             default current_timestamp,
    updated_at   timestamp             default current_timestamp
);

create table publisher_account_role
(
    id                   bigint not null primary key,
    publisher_account_id bigint not null,
    publisher_role_id    bigint not null,
    foreign key (publisher_account_id) references publisher_account (id),
    foreign key (publisher_role_id) references publisher_role (id)
);

-- END PUBLISHER --

-- GAME --

create table game
(
    id           bigint              not null primary key,
    name         varchar(255)        not null,
    publisher_id bigint              not null,
    price        money               not null,
    age          smallint            not null,
    status       smallint  default 0 not null,
    showcase_id  varchar(64)         not null,
    created_at   timestamp default current_timestamp,
    updated_at   timestamp default current_timestamp
);

create table genre
(
    id   bigint       not null primary key,
    name varchar(255) not null,
    slug varchar(255) not null unique
);

create table game_genre
(
    id       bigint not null primary key,
    game_id  bigint not null,
    genre_id bigint not null,
    foreign key (game_id) references game (id),
    foreign key (genre_id) references genre (id)
);

ALTER TABLE game_genre
    ADD CONSTRAINT game_genre.unique UNIQUE (game_id, genre_id);




