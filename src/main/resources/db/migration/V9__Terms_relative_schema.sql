create table terms
(
    id        bigint auto_increment
        primary key,
    version   int                                  not null,
    start_date datetime   default CURRENT_TIMESTAMP not null,
    end_date   datetime                             null,
    is_active tinyint(1) default 1                 not null
);

create table terms_content
(
    id       bigint auto_increment
        primary key,
    content  text                     not null,
    language varchar(16) default 'fr' not null,
    terms_id bigint                   not null,
    constraint terms_content_terms_fk
        foreign key (terms_id) references terms (id)
            on delete cascade
);

create table user_consent
(
    id           bigint auto_increment
        primary key,
    collect_date datetime default CURRENT_TIMESTAMP not null,
    username      varchar(255)                       not null,
    terms_id     bigint                             not null,
    constraint user_consent_unique
        unique (username, terms_id),
    constraint user_consent_terms_fk
        foreign key (terms_id) references terms (id)
            on delete cascade
);

