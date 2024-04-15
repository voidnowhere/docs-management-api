package fr.norsys.docmanagementapi.controller;

import fr.norsys.docmanagementapi.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocController {
    private final DocService docService;
}
