ALTER TABLE doc
    RENAME COLUMN creationDate TO creation_date;
ALTER TABLE additional_metadata
    RENAME COLUMN id TO doc_id;