package com.gradlic.fts.erp.service;

import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);

}
