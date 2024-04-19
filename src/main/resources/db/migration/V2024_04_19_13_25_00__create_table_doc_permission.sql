CREATE TYPE file_permission AS ENUM ('read', 'write');
create table doc_permission
(
    user_id         uuid not null,
    user_email      varchar(100) not null,
    doc_id          uuid not null references doc (id) on delete cascade,
    permission_type file_permission not null
);
