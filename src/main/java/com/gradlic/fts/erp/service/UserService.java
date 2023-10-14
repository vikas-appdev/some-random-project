package com.gradlic.fts.erp.service;

import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.form.UpdateForm;

public interface UserService {
    UserDTO createUser(User user);

    UserDTO getUserByUserEmail(String email);

    void sendVerificationCode(UserDTO user);

    UserDTO verifyCode(String email, String code);

    void resetPassword(String email);

    UserDTO verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    UserDTO verifyAccountKey(String key);

    UserDTO updateUserDetails(UpdateForm user);

    UserDTO getUserByUserId(Long userId);

    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);

    void updateUserRole(Long userDTOId, String roleName);

    void updateAccountSettings(Long id, Boolean enabled, Boolean notLocked);
}
