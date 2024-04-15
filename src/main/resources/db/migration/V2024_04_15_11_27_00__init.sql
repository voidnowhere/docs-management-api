CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table doc
(
    id           uuid primary key default uuid_generate_v4(),
    title        varchar(100),
    type         varchar(50),
    creationDate timestamp        default current_timestamp
);

create table additional_metadata
(
    id    uuid references doc (id),
    key   varchar(50),
    value varchar(50)
)