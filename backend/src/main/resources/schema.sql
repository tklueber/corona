create table if not exists region
(
    id   varchar(10) not null
        constraint region_pk
            primary key,
    name varchar(50) not null,
    eu   boolean     not null
);

alter table region
    owner to corona;

create unique index if not exists region_id_uindex
    on region (id);

create unique index if not exists region_name_uindex
    on region (name);

create table if not exists viruscases
(
    id      serial      not null
        constraint viruscases_pk
            primary key,
    date    date        not null,
    country varchar(10) not null
        constraint viruscases_region_id_fk
            references region,
    cases   integer,
    deaths  integer
);

alter table viruscases
    owner to corona;

create unique index if not exists viruscases_id_uindex
    on viruscases (id);

create unique index if not exists date_country
    on viruscases (country, date);

