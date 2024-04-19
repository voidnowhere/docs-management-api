CREATE TYPE file_permission AS ENUM ('READ', 'WRITE');
create table doc_permission
(
    user_id         uuid            not null,
    user_email      varchar(100)    not null,
    doc_id          uuid            not null references doc (id) on delete cascade,
    permission_type file_permission not null,
    constraint doc_permission_user_id_user_email_key unique (doc_id, user_id)
);
