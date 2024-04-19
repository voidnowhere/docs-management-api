package fr.norsys.docmanagementapi.service;


import fr.norsys.docmanagementapi.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements IAuthenticationFacade {
    public static final String REALM_NAME = "doc-management";
    Keycloak keycloak;

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getUserId() {
        return getAuthentication().getName();
    }

    public Boolean isOwner(UUID ownerId) {
        String userId = getUserId();
        return ownerId.toString().equals(userId);
    }

    public List<UserResponse> getAllUsers() {
        List<UserRepresentation> users = keycloak.realm(REALM_NAME).users().list();
        return users.stream().map(userRepresentation -> UserResponse.builder()
                        .id(UUID.fromString(userRepresentation.getId()))
                        .email(userRepresentation.getEmail())
                        .build())
                .toList();

    }
}
