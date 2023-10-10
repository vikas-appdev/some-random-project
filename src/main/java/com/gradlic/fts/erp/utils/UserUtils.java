package com.gradlic.fts.erp.utils;

import com.gradlic.fts.erp.domain.UserPrincipal;
import com.gradlic.fts.erp.dto.UserDTO;
import org.springframework.security.core.Authentication;

public class UserUtils {
    public static UserDTO getAuthenticatedUser(Authentication authentication){
        return ((UserDTO) authentication.getPrincipal());
    }

    public static UserDTO getLoggedInUser(Authentication authentication){
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
