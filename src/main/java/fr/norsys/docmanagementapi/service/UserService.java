package fr.norsys.docmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String REALM_NAME = "doc-management";
    private final Keycloak keycloak;

    public List<UserRepresentation> getAllUsers(List<String> emails) {
        return keycloak
                .realm(REALM_NAME)
                .users().list()
                .stream()
                .filter(user -> emails.contains(user.getEmail()))
                .toList();
    }
}
