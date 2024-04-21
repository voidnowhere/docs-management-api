package fr.norsys.docmanagementapi.dto;

import fr.norsys.docmanagementapi.entity.PermissionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DocPermissionDto(
        @Email
        String email,
        @NotNull
        PermissionType permission
) {
}
