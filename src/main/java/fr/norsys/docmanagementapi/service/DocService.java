package fr.norsys.docmanagementapi.service;

import fr.norsys.docmanagementapi.dto.DocPostRequest;
import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.entity.Doc;
import fr.norsys.docmanagementapi.entity.Metadata;
import fr.norsys.docmanagementapi.repository.DocRepository;
import fr.norsys.docmanagementapi.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocService {
    private final DocRepository docRepository;
    private final MetadataRepository metadataRepository;

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

    public DocResponse findById(UUID id) {
        Doc doc = docRepository.findById(id);
        return DocResponse
                .builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .creationDate(doc.getCreationDate())
                .metadata(doc.getMetadata())
                .build();
    }

    public List<DocResponse> searchByKeyword(String keyword) {
        List<Doc> docs = docRepository.searchByKeyword(keyword);
        return docs.stream().map(doc -> DocResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .creationDate(doc.getCreationDate())
                .metadata(doc.getMetadata())
                .build()
        ).toList();
    }

    public void createDoc(DocPostRequest docPostRequest) {
        Doc doc = Doc
                .builder()
                .title(docPostRequest.title())
                .type(docPostRequest.type())
                .build();
        doc.setId(docRepository.createDoc(doc));

        Set<Metadata> metadata = new HashSet<>();
        docPostRequest
                .metadata()
                .forEach(m -> metadata.add(new Metadata(doc.getId(), m.key(), m.value())));

        metadataRepository.bulkCreateMetadata(metadata);
    }

    public void deleteDoc(UUID docId) {
        docRepository.deleteDoc(docId);
    }
}
