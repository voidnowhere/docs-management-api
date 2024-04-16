package fr.norsys.docmanagementapi.repository;

import fr.norsys.docmanagementapi.entity.Metadata;
import fr.norsys.docmanagementapi.tables.records.MetadataRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

import static fr.norsys.docmanagementapi.Tables.METADATA;

@Repository
@RequiredArgsConstructor
public class MetadataRepository {
    private final DSLContext dslContext;

    public void bulkCreateMetadata(Set<Metadata> metadata) {
        Set<MetadataRecord> metadataRecords = metadata
                .stream()
                .map(m -> {
                    MetadataRecord metadataRecord = dslContext.newRecord(METADATA);
                    metadataRecord.set(METADATA.DOC_ID, m.getDocId());
                    metadataRecord.set(METADATA.KEY, m.getKey());
                    metadataRecord.set(METADATA.VALUE, m.getValue());
                    return metadataRecord;
                }).collect(Collectors.toSet());

        dslContext.batchInsert(metadataRecords).execute();
    }
}
