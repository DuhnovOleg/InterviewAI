create table assessment_templates (
    id uuid primary key,
    owner_hr_user_id uuid not null,
    title varchar(255) not null,
    description text,
    profession varchar(255),
    level varchar(100),
    public_token varchar(128) not null unique,
    voice_required boolean not null default false,
    camera_required boolean not null default false,
    recording_required boolean not null default false,
    text_answers_allowed boolean not null default true,
    active boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table assessment_questions (
    id uuid primary key,
    template_id uuid not null references assessment_templates(id) on delete cascade,
    question_text text not null,
    position int not null,
    expected_answer text,
    skill_tag varchar(255),
    required boolean not null default true,
    created_at timestamptz not null
);

create table assessment_attempts (
    id uuid primary key,
    template_id uuid not null references assessment_templates(id),
    candidate_name varchar(255) not null,
    candidate_email varchar(255),
    status varchar(50) not null,
    current_position int not null default 1,
    result_token varchar(128) unique,
    report_id uuid,
    overall_score numeric(5,2),
    recommendation varchar(255),
    started_at timestamptz not null,
    completed_at timestamptz,
    created_at timestamptz not null
);

create table assessment_answers (
    id uuid primary key,
    attempt_id uuid not null references assessment_attempts(id) on delete cascade,
    question_id uuid not null references assessment_questions(id),
    question_text text not null,
    answer_text text,
    input_type varchar(50) not null,
    overall_score numeric(5,2),
    correctness_score numeric(5,2),
    completeness_score numeric(5,2),
    clarity_score numeric(5,2),
    relevance_score numeric(5,2),
    grammar_score numeric(5,2),
    feedback text,
    created_at timestamptz not null
);

create table assessment_media_files (
    id uuid primary key,
    attempt_id uuid not null references assessment_attempts(id) on delete cascade,
    answer_id uuid references assessment_answers(id) on delete cascade,
    media_type varchar(50) not null,
    storage_key text not null,
    original_filename varchar(255),
    mime_type varchar(255),
    size_bytes bigint,
    created_at timestamptz not null
);

create index idx_templates_owner on assessment_templates(owner_hr_user_id);
create index idx_templates_token on assessment_templates(public_token);
create index idx_attempts_template on assessment_attempts(template_id);
create index idx_attempts_result_token on assessment_attempts(result_token);
