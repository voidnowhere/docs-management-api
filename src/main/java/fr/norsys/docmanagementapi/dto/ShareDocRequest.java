package fr.norsys.docmanagementapi.dto;

import fr.norsys.docmanagementapi.entity.PermissionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ShareDocRequest(
        @NotNull
        PermissionType permissionType,
        @NotEmpty
        List<String> emails
) {
}
