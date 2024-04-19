package fr.norsys.docmanagementapi.repository;

import fr.norsys.docmanagementapi.entity.Doc;
import fr.norsys.docmanagementapi.entity.Metadata;
import fr.norsys.docmanagementapi.exception.DocNotFoundException;
import fr.norsys.docmanagementapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static fr.norsys.docmanagementapi.Tables.*;

@Repository
@RequiredArgsConstructor
public class DocRepository {
    private final DSLContext dslContext;
    private final AuthService authService;

    public List<Doc> findAll() {
        List<Doc> docs = dslContext.select(DOC.ID, DOC.TITLE, DOC.CREATION_DATE, DOC.TYPE)
                .from(DOC)
                .where(ownerCondition())
                .fetchInto(Doc.class);
        return setMetadataToDocs(docs);
    }

    public Doc findById(UUID id) {
        Doc doc = Optional.ofNullable(dslContext
                        .fetchOne(DOC, DOC.ID
                                .eq(id)))
                .orElseThrow(DocNotFoundException::new)
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
                .where(ownerCondition())
                .and((getSearchCondition(likeExpression)))
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

    public void createDoc(Doc doc) {
        dslContext
                .insertInto(DOC)
                .set(DOC.ID, doc.getId())
                .set(DOC.TITLE, doc.getTitle())
                .set(DOC.TYPE, doc.getType())
                .set(DOC.PATH, doc.getPath())
                .set(DOC.CHECKSUM, doc.getChecksum())
                .set(DOC.OWNER_ID, doc.getOwnerId())
                .execute();
    }

    public void deleteDoc(UUID docId) {
        dslContext
                .delete(DOC)
                .where(DOC.ID.eq(docId))
                .execute();
    }

    public String getDocPath(UUID docId) {
        return Optional.ofNullable(dslContext
                .select(DOC.PATH)
                .from(DOC)
                .where(DOC.ID.eq(docId))
                .fetchOneInto(Doc.class)
        ).orElseThrow(DocNotFoundException::new).getPath();
    }

    public UUID getDocOwnerId(UUID docId) {
        return Optional.ofNullable(dslContext
                .select(DOC.OWNER_ID)
                .from(DOC)
                .where(DOC.ID.eq(docId))
                .fetchOneInto(Doc.class)
        ).orElseThrow(DocNotFoundException::new).getOwnerId();
    }

    public boolean isDocChecksumExists(String fileChecksum) {
        return dslContext.fetchExists(dslContext
                .selectOne()
                .from(DOC)
                .where(DOC.CHECKSUM.eq(fileChecksum))
        );
    }

    private Condition getSearchCondition(String likeExpression) {
        return DOC.TITLE.likeIgnoreCase(likeExpression)
                .or(DOC.CREATION_DATE.likeIgnoreCase(likeExpression))
                .or(DOC.TYPE.likeIgnoreCase(likeExpression))
                .or(METADATA.VALUE.likeIgnoreCase(likeExpression))
                .or(METADATA.DOC_ID.isNotNull());
    }

    private Condition ownerCondition() {
        return DOC.OWNER_ID.eq(authService.getCurrentUserId());
    }

    public List<Doc> getSharedDocs() {
        List<Doc> docs = dslContext.select(DOC.ID, DOC.TITLE, DOC.CREATION_DATE, DOC.TYPE)
                .from(DOC)
                .rightJoin(DOC_PERMISSION)
                .on(DOC_PERMISSION.DOC_ID.eq(DOC.ID))
                .where(DOC_PERMISSION.USER_ID.eq(authService.getCurrentUserId()))
                .fetchInto(Doc.class);
        return setMetadataToDocs(docs);
    }

}
