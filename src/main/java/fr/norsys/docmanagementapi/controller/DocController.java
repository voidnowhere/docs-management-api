package fr.norsys.docmanagementapi.controller;

import fr.norsys.docmanagementapi.dto.DocResponse;
import fr.norsys.docmanagementapi.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
