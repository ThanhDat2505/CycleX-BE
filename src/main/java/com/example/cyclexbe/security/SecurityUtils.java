package com.example.cyclexbe.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Utility class for security operations
 * Kiểm tra quyền truy cập resource của user
 */
@Component
public class SecurityUtils {

    /**
     * Get authenticated user ID from JWT token
     * @return userId from token subject
     */
    public static String getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return auth.getName(); // Spring Security convention: principal name is subject
    }

    /**
     * Validate that the authenticated user matches the requested resource owner
     * Bảo vệ: Người dùng không thể truy cập resource của người khác
     *
     * @param requestedUserId - ID từ path variable
     * @param resourceOwnerType - Loại resource ("SELLER", "INSPECTOR")
     * @throws ResponseStatusException 403 nếu không match
     */
    public static void validateResourceOwner(String requestedUserId, String resourceOwnerType) {
        String authenticatedUserId = getAuthenticatedUserId();

        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You don't have permission to access this " + resourceOwnerType + "'s resources"
            );
        }
    }

    /**
     * Get authenticated user role
     * @return role như "ROLE_SELLER", "ROLE_INSPECTOR", "ROLE_ADMIN"
     */
    public static String getAuthenticatedUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return auth.getAuthorities().stream()
                .findFirst()
                .map(ga -> ga.getAuthority())
                .orElse("UNKNOWN");
    }
}
