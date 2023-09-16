package com.gradlic.fts.erp.dto;

import com.gradlic.fts.erp.domain.Address;
import com.gradlic.fts.erp.domain.Organisation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email; // UNIQUE
    private String profileImageUrl; // CDN flaticon avtar 512 149
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastLoginDateDisplay;
    private LocalDateTime createAt;
    private String role;
    private boolean isActive; // enabled
    private boolean isNotLocked;
    private String userNFCIdCardNumber;
    private String aadhaarNumber;
    private String mobileNumber;
    private String title;
    private String bio;
    private boolean isUsingMFA; // Multi factor authentication
    private Address address;
    private Set<Organisation> organisations;
}
