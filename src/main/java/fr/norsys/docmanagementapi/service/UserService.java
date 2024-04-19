package fr.norsys.docmanagementapi.service;

import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private static final String REALM_NAME = "doc-management";
    Keycloak keycloak;

    public List<UserRepresentation> getAllUsers(List<String> emails) {
        return keycloak
                .realm(REALM_NAME)
                .users().list()
                .stream()
                .filter(user -> emails.contains(user.getEmail()))
                .toList();
    }
}
