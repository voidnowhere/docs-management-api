package fr.norsys.docmanagementapi.entity;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    private String key;
    private String value;
    private UUID docId;
}
