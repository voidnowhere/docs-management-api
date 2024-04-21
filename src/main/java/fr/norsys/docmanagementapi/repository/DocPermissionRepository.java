package fr.norsys.docmanagementapi.repository;

import fr.norsys.docmanagementapi.entity.DocPermission;
import fr.norsys.docmanagementapi.enums.FilePermission;
import fr.norsys.docmanagementapi.tables.records.DocPermissionRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.norsys.docmanagementapi.Tables.DOC_PERMISSION;

@Repository
@RequiredArgsConstructor
public class DocPermissionRepository {
    private final DSLContext dslContext;

    public void bulkCreateDocPermission(Set<DocPermission> docPermissions) {
        Set<DocPermissionRecord> docPermissionRecords = docPermissions
                .stream()
                .map(d -> {
                    DocPermissionRecord docPermissionRecord = dslContext.newRecord(DOC_PERMISSION);
                    docPermissionRecord.setDocId(d.getDocId());
                    docPermissionRecord.setUserId(d.getUserId());
                    docPermissionRecord.setUserEmail(d.getUserEmail());
                    docPermissionRecord.setPermissionType(
                            FilePermission.valueOf(d.getPermissionType().toString())
                    );
                    return docPermissionRecord;
                }).collect(Collectors.toSet());

        dslContext.batchInsert(docPermissionRecords).execute();
    }

    public void deleteDocPermissionByDocId(UUID docId) {
        dslContext
                .deleteFrom(DOC_PERMISSION)
                .where(DOC_PERMISSION.DOC_ID.eq(docId))
                .execute();
    }

    public boolean isUserIdExistsInDocPermission(UUID docId, UUID userId) {
        return dslContext.fetchExists(dslContext
                .selectOne()
                .from(DOC_PERMISSION)
                .where(DOC_PERMISSION.DOC_ID.eq(docId))
                .and(DOC_PERMISSION.USER_ID.eq(userId))
        );
    }

    public List<DocPermission> getDocPermissions(UUID docId) {
        return dslContext
                .select(DOC_PERMISSION.USER_EMAIL, DOC_PERMISSION.PERMISSION_TYPE)
                .from(DOC_PERMISSION)
                .where(DOC_PERMISSION.DOC_ID.eq(docId))
                .fetchInto(DocPermission.class);
    }
}
