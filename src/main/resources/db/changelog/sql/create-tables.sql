create table if not exists public.users
(
    id         bigint       not null
    primary key,
    email      varchar(100) not null
    unique,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    password   varchar(255) not null,
    role       varchar(255)
    constraint users_role_check
    check ((role)::text = ANY ((ARRAY ['USER'::character varying, 'ADMIN'::character varying])::text[]))
    );

create index if not exists email_idx
    on public.users (email);

create table if not exists public.credit_card
(
    balance     numeric(38)  not null,
    expiry_date date         not null,
    to_block    boolean      not null,
    id          bigint       not null
    primary key,
    user_id     bigint
    constraint fkes0kii3rdngv6p26vsih412jy
    references public.users,
    card_holder varchar(255) not null,
    card_number varchar(255) not null
    unique,
    status      varchar(255) not null
    constraint credit_card_status_check
    check ((status)::text = ANY
((ARRAY ['ACTIVE'::character varying, 'BLOCKED'::character varying, 'EXPIRED'::character varying])::text[]))
    );

create sequence if not exists public.users_seq
    increment by 50;

create sequence if not exists public.credit_card_seq
    increment by 50;