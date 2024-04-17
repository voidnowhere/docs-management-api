alter table doc
    alter column title set not null;
alter table doc
    alter column type set not null;
alter table doc
    alter column creation_date set not null;

alter table metadata
    alter column doc_id set not null;
alter table metadata
    drop constraint additional_metadata_id_fkey;
alter table metadata
    add constraint metadata_id_doc_fkey foreign key (doc_id) references doc (id) on delete cascade;
alter table metadata
    alter column key set not null;
alter table metadata
    alter column value set not null;