package fr.norsys.docmanagementapi.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Metadata {
    private UUID docId;
    private String key;
    private String value;
}
