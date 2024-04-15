package fr.norsys.docmanagementapi.service;

import fr.norsys.docmanagementapi.repository.DocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocService {
    private final DocRepository docRepository;
}
