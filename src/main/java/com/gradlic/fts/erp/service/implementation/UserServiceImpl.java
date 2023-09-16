package com.gradlic.fts.erp.service.implementation;

import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.dtomapper.UserDTOMapper;
import com.gradlic.fts.erp.repository.UserCRUDRepository;
import com.gradlic.fts.erp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserCRUDRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }
}
