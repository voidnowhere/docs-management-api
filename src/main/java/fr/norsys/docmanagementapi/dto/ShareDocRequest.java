package fr.norsys.docmanagementapi.dto;

import jakarta.validation.Valid;

import java.util.List;

public record ShareDocRequest(
        @Valid
        List<DocPermissionDto> users
) {
}
