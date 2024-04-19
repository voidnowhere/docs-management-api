package fr.norsys.docmanagementapi.service;


import fr.norsys.docmanagementapi.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@Component
public class AuthService implements IAuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UUID getCurrentUserId() {
        return UUID.fromString(getAuthentication().getName());
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
