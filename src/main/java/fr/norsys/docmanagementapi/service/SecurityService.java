package fr.norsys.docmanagementapi.service;

import fr.norsys.docmanagementapi.repository.DocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityService {
    private final AuthService authService;
    private final DocRepository docRepository;

    public boolean isDocBelongToCurrentUser(UUID docId) {
        UUID currentUserId = authService.getCurrentUserId();
        UUID docOwnerId = docRepository.getDocOwnerId(docId);

        return currentUserId.equals(docOwnerId);
    }
}
