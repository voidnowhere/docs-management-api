package fr.norsys.docmanagementapi.repository;

import fr.norsys.docmanagementapi.entity.Metadata;
import fr.norsys.docmanagementapi.entity.Doc;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static fr.norsys.docmanagementapi.Tables.METADATA;
import static fr.norsys.docmanagementapi.Tables.DOC;

@Repository
@RequiredArgsConstructor
public class DocRepository {
    private final DSLContext dslContext;

    public List<Doc> findAll() {
        List<Doc> docs = dslContext.selectFrom(DOC).fetchInto(Doc.class);
        return setMetadataToDocs(docs);
    }

    public Doc findById(UUID id) {
        Doc doc = Optional.ofNullable(dslContext
                        .fetchOne(DOC, DOC.ID
                                .eq(id)))
                .orElseThrow(NoSuchElementException::new)
                .into(Doc.class);

        doc.setMetadata(fetchMetadataForDoc(doc.getId()));
        return doc;
    }

    public List<Doc> searchByKeyword(String keyword) {
        String likeExpression = "%" + keyword + "%";

        List<Doc> docs = dslContext.select(DOC.ID, DOC.TITLE, DOC.CREATION_DATE, DOC.TYPE)
                .from(DOC)
                .leftJoin(METADATA)
                .on(METADATA.DOC_ID.eq(DOC.ID)
                        .and(METADATA.VALUE.likeIgnoreCase(likeExpression)))
                .where(getSearchCondition(likeExpression))
                .fetchInto(Doc.class);

        return setMetadataToDocs(docs);
    }

    private List<Doc> setMetadataToDocs(List<Doc> docs) {
        Set<UUID> docIds = docs.stream().map(Doc::getId).collect(Collectors.toSet());

        List<Metadata> metadata = dslContext
                .selectFrom(METADATA)
                .where(METADATA.DOC_ID.in(docIds))
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

    private Map<String, String> fetchMetadataForDoc(UUID docId) {
        List<Metadata> metadata = dslContext
                .selectFrom(METADATA)
                .where(METADATA.DOC_ID.eq(docId))
                .fetchInto(Metadata.class);

        return metadata.stream()
                .collect(Collectors.toMap(Metadata::getKey, Metadata::getValue));
    }

    private Condition getSearchCondition(String likeExpression) {
        return DOC.TITLE.likeIgnoreCase(likeExpression)
                .or(DOC.CREATION_DATE.likeIgnoreCase(likeExpression))
                .or(DOC.TYPE.likeIgnoreCase(likeExpression))
                .or(METADATA.VALUE.likeIgnoreCase(likeExpression))
                .or(METADATA.DOC_ID.isNotNull());
    }

    public UUID createDoc(Doc doc) {
        return dslContext
                .insertInto(DOC)
                .set(DOC.TITLE, doc.getTitle())
                .set(DOC.TYPE, doc.getType())
                .returningResult(DOC.ID)
                .fetchOneInto(UUID.class);
    }

    public void deleteDoc(UUID docId) {
        dslContext
                .delete(DOC)
                .where(DOC.ID.eq(docId))
                .execute();
    }
}
