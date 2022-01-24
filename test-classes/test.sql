create schema test;

create table test.framework
(
    id       identity primary key,
    name     varchar(255) not null,
    language varchar(255),
    link     varchar(255),
    creationDate timestamp
);

insert into test.framework (name, language, link, creationDate)
values ('Spring Framework', 'Java', 'https://spring.io', '2017-10-01 21:22:23'),
       ('Angular', 'JavaScript', 'https://vuejs.org', '2017-10-02 21:22:23'),
       ('Laravel', 'PHP', 'https://laravel.com', '2017-10-03 03:22:23'),
       ('Hibernate', 'Java', 'https://hibernate.org', '2017-10-04 21:22:23');