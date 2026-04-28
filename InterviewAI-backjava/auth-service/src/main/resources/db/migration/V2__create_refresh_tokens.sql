create table refresh_tokens (
                                id uuid primary key,
                                user_id uuid not null,
                                token_hash varchar(255) not null unique,
                                expires_at timestamptz not null,
                                revoked boolean not null default false,
                                created_at timestamptz not null
);

create index idx_refresh_tokens_user_id on refresh_tokens(user_id);
create index idx_refresh_tokens_hash on refresh_tokens(token_hash);