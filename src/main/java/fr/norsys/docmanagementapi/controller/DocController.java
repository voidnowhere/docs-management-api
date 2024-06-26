package fr.norsys.docmanagementapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.norsys.docmanagementapi.dto.DocPostRequest;
import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.dto.MetadataDto;
import fr.norsys.docmanagementapi.dto.ShareDocRequest;
import fr.norsys.docmanagementapi.exception.MethodArgumentNotValidExceptionHandler;
import fr.norsys.docmanagementapi.service.DocService;
import fr.norsys.docmanagementapi.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocController implements MethodArgumentNotValidExceptionHandler {
    private final DocService docService;
    private final ObjectMapper objectMapper;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<List<DocResponse>> findAll(
            @RequestParam(required = false, name = "keyword") Optional<String> optionalKeyword
    ) {
        List<DocResponse> docs;

        if (optionalKeyword.isEmpty() || optionalKeyword.get().isEmpty()) {
            docs = docService.findAll();
        } else {
            docs = docService.searchByKeyword(optionalKeyword.get());
        }

        return docs.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(docs);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<DocResponse>> searchByKeyword(@PathVariable String keyword) {
        List<DocResponse> docs = docService.searchByKeyword(keyword);
        return docs.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(docs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocResponse> findById(@PathVariable UUID id) {
        DocResponse docResponse = docService.findById(id);

        return ResponseEntity.ok(docResponse);
    }

    @PostMapping
    public ResponseEntity<Void> createDoc(
            @RequestPart MultipartFile file,
            @RequestParam String metadata
    ) throws IOException {
        Set<MetadataDto> metadataDto = objectMapper.readValue(metadata, new TypeReference<>() {
        });
        docService.createDoc(new DocPostRequest(file, metadataDto));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{docId}")
    @PreAuthorize("@securityService.isDocBelongToCurrentUser(#docId)")
    public ResponseEntity<Void> deleteDoc(@PathVariable UUID docId) throws IOException {
        docService.deleteDoc(docId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{docId}/download")
    @PreAuthorize("""
            @securityService.isDocBelongToCurrentUser(#docId) or
            @securityService.hasPermissionToDocDownload(#docId)
            """)
    public ResponseEntity<Resource> downloadDoc(@PathVariable UUID docId) throws MalformedURLException {
        Resource file = docService.downloadDoc(docId);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\""
                ).body(file);
    }

    @PostMapping("/{docId}/share")
    @PreAuthorize("@securityService.isDocBelongToCurrentUser(#docId)")
    public ResponseEntity<Void> shareDoc(
            @PathVariable UUID docId,
            @Valid @RequestBody ShareDocRequest shareDocRequest
    ) {
        docService.shareDoc(docId, shareDocRequest);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shared-docs")
    public ResponseEntity<List<DocResponse>> getSharedDocs() {
        List<DocResponse> docs = docService.getSharedDocs();
        return docs.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(docs);
    }

}
