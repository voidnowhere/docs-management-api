package fr.norsys.docmanagementapi.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Doc {
    private UUID id;
    private String title;
    private LocalDateTime creationDate;
    private Map<String, String> metadata;
}
