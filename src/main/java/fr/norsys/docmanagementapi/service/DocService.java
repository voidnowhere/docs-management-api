package fr.norsys.docmanagementapi.service;

import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.entity.Doc;
import fr.norsys.docmanagementapi.repository.DocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocService {
    private final DocRepository docRepository;

    public List<DocResponse> findAll() {
        List<Doc> docs = docRepository.findAll();
        return docs.stream().map(doc -> DocResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .creationDate(doc.getCreationDate())
                .metadata(doc.getMetadata())
                .build()
        ).toList();

    }
}
