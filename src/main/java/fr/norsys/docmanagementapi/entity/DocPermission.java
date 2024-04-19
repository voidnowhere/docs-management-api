package fr.norsys.docmanagementapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocPermission {
    private UUID docId;
    private UUID userId;
    private String userEmail;
    private PermissionType permissionType;
}
