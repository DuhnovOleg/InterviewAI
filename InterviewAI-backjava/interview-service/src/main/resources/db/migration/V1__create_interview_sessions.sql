create table interview_sessions (
                                    id uuid primary key,
                                    user_id uuid not null,
                                    python_session_id varchar(64) not null unique,
                                    profession varchar(255),
                                    level varchar(100),
                                    total_questions int,
                                    status varchar(50),
                                    awaiting_stop_confirmation boolean default false,
                                    started_at timestamptz not null,
                                    completed_at timestamptz,
                                    created_at timestamptz not null,
                                    updated_at timestamptz not null
);