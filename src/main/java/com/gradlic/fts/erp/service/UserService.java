package com.gradlic.fts.erp.service;

import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);

    UserDTO getUserByUserEmail(String email);

    void sendVerificationCode(UserDTO user);

    UserDTO verifyCode(String email, String code);

    void resetPassword(String email);

    UserDTO verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    UserDTO verifyAccountKey(String key);
}
