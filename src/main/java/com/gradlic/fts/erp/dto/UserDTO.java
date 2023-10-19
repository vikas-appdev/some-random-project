package com.gradlic.fts.erp.dto;

import com.gradlic.fts.erp.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// @Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String userId; // extra
    private String firstName;
    private String lastName;
    private String username; // extra
    private String email; // UNIQUE
    private String profileImageUrl; // CDN flaticon avtar 512 149
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastLoginDateDisplay;
    private LocalDateTime createdAt;
    private boolean isActive; // enabled
    private boolean isNotLocked;
    private String userNFCIdCardNumber;
    private String aadhaarNumber;
    private String mobileNumber;
    private String title;
    private String bio;
    private boolean isUsingMFA; // Multi factor authentication
    private Address address;
    // private Set<Organisation> organisations;

    private String roleName;
    private String permissions;

}
