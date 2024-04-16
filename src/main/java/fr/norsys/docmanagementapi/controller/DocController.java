package fr.norsys.docmanagementapi.controller;

import fr.norsys.docmanagementapi.dto.DocPostRequest;
import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.exception.MethodArgumentNotValidExceptionHandler;
import fr.norsys.docmanagementapi.service.DocService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocController implements MethodArgumentNotValidExceptionHandler {
    private final DocService docService;

    @GetMapping
    public ResponseEntity<List<DocResponse>> findAll(
            @RequestParam(required = false, name = "keyword") Optional<String> optionalKeyword
    ) {
        List<DocResponse> docs;

        if (optionalKeyword.isEmpty()) {
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
            @Valid @RequestBody DocPostRequest docPostRequest
    ) {
        docService.createDoc(docPostRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> deleteDoc(@PathVariable UUID docId) {
        docService.deleteDoc(docId);

        return ResponseEntity.noContent().build();
    }
}
