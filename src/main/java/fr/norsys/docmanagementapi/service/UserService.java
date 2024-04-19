package fr.norsys.docmanagementapi.service;


import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Service
public class UserService implements IAuthenticationFacade {

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
}
