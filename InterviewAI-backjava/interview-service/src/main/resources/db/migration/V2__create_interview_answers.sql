create table interview_answers (
                                   id uuid primary key,
                                   interview_session_id uuid not null,
                                   question_number int,
                                   question_text text,
                                   answer_text text,
                                   overall_score numeric(4,2),
                                   feedback text,
                                   input_type varchar(50),
                                   answered_at timestamptz not null
);