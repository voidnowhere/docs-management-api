package fr.norsys.docmanagementapi.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DocRepository {
    private final DSLContext dslContext;
}
