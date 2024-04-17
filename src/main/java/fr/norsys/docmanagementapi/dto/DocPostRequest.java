package fr.norsys.docmanagementapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record DocPostRequest(
        @NotNull
        MultipartFile file,
        @Valid
        Set<MetadataDto> metadata
) {
}
