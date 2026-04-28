create table interview_reports (
                                   id uuid primary key,
                                   interview_session_id uuid not null unique,
                                   overall_score numeric(4,2),
                                   technical_score numeric(4,2),
                                   correctness_score numeric(4,2),
                                   completeness_score numeric(4,2),
                                   clarity_score numeric(4,2),
                                   relevance_score numeric(4,2),
                                   grammar_score numeric(4,2),
                                   confidence_score numeric(4,2),
                                   response_speed_score numeric(4,2),
                                   hire_recommendation varchar(255),
                                   recommended_level varchar(100),
                                   summary text,
                                   report_json text,
                                   created_at timestamptz not null
);