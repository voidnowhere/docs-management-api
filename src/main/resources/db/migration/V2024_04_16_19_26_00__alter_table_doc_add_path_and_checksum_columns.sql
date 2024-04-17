alter table doc
    add path varchar(255);
alter table doc
    add checksum varchar(64) unique;