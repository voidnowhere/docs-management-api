package fr.norsys.docmanagementapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MetadataDto(
        @NotBlank
        @Size(max = 50)
        String key,
        @NotBlank
        @Size(max = 50)
        String value
) {
}
