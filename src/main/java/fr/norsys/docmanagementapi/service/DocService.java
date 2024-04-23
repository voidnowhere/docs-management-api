package fr.norsys.docmanagementapi.service;

import fr.norsys.docmanagementapi.dto.DocPermissionDto;
import fr.norsys.docmanagementapi.dto.DocPostRequest;
import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.dto.ShareDocRequest;
import fr.norsys.docmanagementapi.entity.Doc;
import fr.norsys.docmanagementapi.entity.DocPermission;
import fr.norsys.docmanagementapi.entity.Metadata;
import fr.norsys.docmanagementapi.entity.PermissionType;
import fr.norsys.docmanagementapi.exception.DocAlreadyExistException;
import fr.norsys.docmanagementapi.repository.DocPermissionRepository;
import fr.norsys.docmanagementapi.repository.DocRepository;
import fr.norsys.docmanagementapi.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocService {
    private final DocRepository docRepository;
    private final MetadataRepository metadataRepository;
    private final StorageService storageService;
    private final AuthService authService;
    private final UserService userService;
    private final DocPermissionRepository docPermissionRepository;

    public List<DocResponse> findAll(UUID ownerId) {
        List<Doc> docs = docRepository.findAll(ownerId);

        return docs.stream().map(doc -> DocResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .type(doc.getType())
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

    public List<DocResponse> searchByKeyword(UUID ownerId, String keyword) {
        List<Doc> docs = docRepository.searchByKeyword(ownerId, keyword);

        return docs.stream().map(doc -> DocResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .creationDate(doc.getCreationDate())
                .metadata(doc.getMetadata())
                .build()
        ).toList();
    }

    @Transactional
    public void createDoc(DocPostRequest docPostRequest) throws IOException {
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

    public void shareDoc(UUID docId, ShareDocRequest shareDocRequest) {
        List<UserRepresentation> users = userService.getAllUsers(shareDocRequest
                .users()
                .stream()
                .map(DocPermissionDto::email).toList()
        );

        Set<DocPermission> docPermissions = new HashSet<>();
        Map<String, PermissionType> shareDocWithUserMap = shareDocRequest
                .users()
                .stream()
                .collect(Collectors.toMap(
                        DocPermissionDto::email,
                        DocPermissionDto::permission
                ));

        users.forEach(user -> docPermissions.add(
                new DocPermission(
                        docId,
                        UUID.fromString(user.getId()),
                        user.getEmail(),
                        shareDocWithUserMap.get(user.getEmail())
                )
        ));

        docPermissionRepository.deleteDocPermissionByDocId(docId);

        docPermissionRepository.bulkCreateDocPermission(docPermissions);
    }

    public List<DocResponse> getSharedDocs(UUID ownerId) {
        List<Doc> docs = docRepository.getSharedDocs(ownerId);

        return docs.stream().map(doc -> DocResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .creationDate(doc.getCreationDate())
                .metadata(doc.getMetadata())
                .build()
        ).toList();
    }

    public List<DocPermissionDto> getDocPermissions(UUID docId) {
        List<DocPermission> docPermissions = docPermissionRepository.getDocPermissions(docId);

        return docPermissions
                .stream()
                .map(p -> new DocPermissionDto(p.getUserEmail(), p.getPermissionType()))
                .toList();
    }
}
