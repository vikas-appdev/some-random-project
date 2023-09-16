package com.gradlic.fts.erp.dtomapper;

import com.gradlic.fts.erp.domain.Person;
import com.gradlic.fts.erp.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper {
    public static UserDTO fromUser(Person user){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static Person toUser(UserDTO userDTO){
        Person person = new Person();
        BeanUtils.copyProperties(userDTO, person);
        return person;
    }
}
