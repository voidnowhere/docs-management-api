package fr.norsys.docmanagementapi.service;

import fr.norsys.docmanagementapi.dto.DocPostRequest;
import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.entity.Doc;
import fr.norsys.docmanagementapi.entity.Metadata;
import fr.norsys.docmanagementapi.exception.DocAlreadyExistException;
import fr.norsys.docmanagementapi.repository.DocRepository;
import fr.norsys.docmanagementapi.repository.MetadataRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocService {
    private static final Logger log = LoggerFactory.getLogger(DocService.class);
    private final DocRepository docRepository;
    private final MetadataRepository metadataRepository;
    private final StorageService storageService;
    private final AuthService authService;

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

    @Transactional
    public void createDoc(@Valid DocPostRequest docPostRequest) throws IOException {
        UUID currentUserId = authService.getCurrentUserId();
        String fileChecksum = storageService.getFileChecksum(docPostRequest.file());
        if (docRepository.isDocChecksumExists(fileChecksum)) {
            throw new DocAlreadyExistException("doc already exist");
        }

        Doc doc = storageService.fillDocInfo(docPostRequest.file());

        doc.setId(UUID.randomUUID());

        String filePath = storageService.storeFile(doc.getId(), docPostRequest.file());
        doc.setPath(filePath);

        doc.setChecksum(fileChecksum);

        doc.setOwnerId(currentUserId);
        docRepository.createDoc(doc);

        Set<Metadata> metadata = new HashSet<>();
        docPostRequest
                .metadata()
                .forEach(m -> metadata.add(new Metadata(doc.getId(), m.key(), m.value())));

        metadataRepository.bulkCreateMetadata(metadata);
    }

    public void deleteDoc(UUID docId) throws IOException {
        storageService.deleteFile(docRepository.getDocPath(docId));
        docRepository.deleteDoc(docId);

    }

    public Resource downloadDoc(UUID docId) throws MalformedURLException {
        String docPath = docRepository.getDocPath(docId);

        return storageService.getFileAsResource(docPath);
    }
}
