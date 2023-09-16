package com.gradlic.fts.erp.service.implementation;

import com.gradlic.fts.erp.domain.Person;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.dtomapper.UserDTOMapper;
import com.gradlic.fts.erp.repository.PersonCRUDRepository;
import com.gradlic.fts.erp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PersonCRUDRepository<Person> userRepository;

    @Override
    public UserDTO createUser(Person user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }
}
