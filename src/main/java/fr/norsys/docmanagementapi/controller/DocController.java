package fr.norsys.docmanagementapi.controller;

import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.service.DocService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocController {
    private final DocService docService;

    @GetMapping
    public ResponseEntity<List<DocResponse>> findAll() {
        List<DocResponse> docs = docService.findAll();
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
        try {
            DocResponse docResponse = docService.findById(id);
            return ResponseEntity.ok(docResponse);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


}