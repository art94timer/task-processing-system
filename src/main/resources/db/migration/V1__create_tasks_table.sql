create table tasks
(
    id uuid primary key,
    name varchar(255) not null,
    description text,
    priority varchar(20) not null,
    status varchar(20) not null,
    execute_at timestamp,
    retry_count integer not null default 0,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);