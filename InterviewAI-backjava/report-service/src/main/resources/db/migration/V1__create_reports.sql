create table reports (
    id uuid primary key,
    source_type varchar(50) not null,
    source_id uuid not null,
    owner_user_id uuid,
    candidate_name varchar(255),
    candidate_email varchar(255),
    public_token varchar(128) not null unique,
    overall_score numeric(5,2),
    recommendation varchar(255),
    summary text,
    report_json text not null,
    created_at timestamptz not null
);

create index idx_reports_owner_user_id on reports(owner_user_id);
create index idx_reports_source on reports(source_type, source_id);
create index idx_reports_public_token on reports(public_token);
