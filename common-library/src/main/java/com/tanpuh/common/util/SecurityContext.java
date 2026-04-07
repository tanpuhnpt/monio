package com.tanpuh.common.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContext {

    public static Long getCurrentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
