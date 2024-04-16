package fr.norsys.docmanagementapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record DocPostRequest(
        @NotBlank
        @Size(max = 100)
        String title,
        @NotBlank
        @Size(max = 50)
        String type,
        @Valid
        Set<MetadataDto> metadata
) {
}
