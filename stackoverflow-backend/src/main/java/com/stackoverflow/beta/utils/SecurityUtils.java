package com.stackoverflow.beta.utils;

import com.stackoverflow.beta.model.dto.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {

    // Utility method to retrieve the userId from the SecurityContext
    public static Optional<Integer> getUserIdFromSecurityContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            int userId = ((CustomUserDetails) principal).getUserId();
            return Optional.of(userId);
        }
        return Optional.empty();
    }
}
