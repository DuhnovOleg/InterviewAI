create table users (
                       id uuid primary key,
                       email varchar(255) not null unique,
                       username varchar(100) not null unique,
                       password_hash varchar(255) not null,
                       enabled boolean not null default true,
                       created_at timestamptz not null,
                       updated_at timestamptz not null
);

create table user_roles (
                            user_id uuid not null,
                            role_name varchar(50) not null
);

alter table user_roles
    add constraint fk_user_roles_user
        foreign key (user_id) references users(id);

create index idx_user_roles_user_id on user_roles(user_id);