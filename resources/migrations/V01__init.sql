create table users (
    id uuid primary key,
    username text not null unique,
    password text not null,
    first_name text not null,
    last_name text not null,
    email_address text not null,
    created_at timestamp not null
);