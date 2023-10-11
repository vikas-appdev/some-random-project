package com.gradlic.fts.erp.repository;

import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.form.UpdateForm;

import java.util.Collection;

public interface UserCRUDRepository<T extends User>{
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

    User getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);

    User verifyCode(String email, String code);

    void resetPassword(String email);

    User verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    User verifyAccountKey(String key);

    User updateUserDetails(UpdateForm user);
}
