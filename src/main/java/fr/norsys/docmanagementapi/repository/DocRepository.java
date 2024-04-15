package fr.norsys.docmanagementapi.repository;

import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.entity.Metadata;
import fr.norsys.docmanagementapi.entity.Doc;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static fr.norsys.docmanagementapi.Tables.ADDITIONAL_METADATA;
import static fr.norsys.docmanagementapi.Tables.DOC;

@Repository
@RequiredArgsConstructor
public class DocRepository {
    private final DSLContext dslContext;

    public List<Doc> findAll() {
        List<Doc> docs = dslContext.selectFrom(DOC).fetchInto(Doc.class);

        Set<UUID> docIds = docs.stream().map(Doc::getId).collect(Collectors.toSet());

        List<Metadata> metadata = dslContext
                .selectFrom(ADDITIONAL_METADATA)
                .where(ADDITIONAL_METADATA.DOC_ID.in(docIds))
                .fetchInto(Metadata.class);

        Map<UUID, List<Metadata>> metadataMap = metadata.stream()
                .collect(Collectors.groupingBy(Metadata::getDocId));

        for (Doc doc : docs) {
            List<Metadata> docMetadata = metadataMap.getOrDefault(doc.getId(), Collections.emptyList());
            Map<String, String> metadataAsMap = docMetadata.stream()
                    .collect(Collectors.toMap(Metadata::getKey, Metadata::getValue));
            doc.setMetadata(metadataAsMap);
        }

        return docs;
    }


}
