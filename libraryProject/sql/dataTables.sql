create table Visitor (
    id int primary key generated by default as identity,
    initials varchar(50) not null,
    age int check (age > 0) not null,
    date_of_birth Date not null,
    email varchar not null
);

create table  Book (
    id int primary key generated by default as identity,
    visitor_id int references Visitor(id) on delete set null,
    name varchar(50) not null,
    author varchar(50) not null,
    year_of_writing Date not null
);