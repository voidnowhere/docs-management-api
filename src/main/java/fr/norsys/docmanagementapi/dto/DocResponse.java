package fr.norsys.docmanagementapi.dto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
public record DocResponse(
        UUID id,
        String title,
        String type,
        LocalDateTime creationDate,
        Map<String, String> metadata

) {
}