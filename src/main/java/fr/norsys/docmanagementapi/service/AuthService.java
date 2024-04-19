package fr.norsys.docmanagementapi.service;


import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
}
