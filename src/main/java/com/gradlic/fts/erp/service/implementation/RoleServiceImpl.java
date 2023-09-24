package com.gradlic.fts.erp.service.implementation;

import com.gradlic.fts.erp.domain.Role;
import com.gradlic.fts.erp.repository.RoleRepository;
import com.gradlic.fts.erp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository<Role> roleRoleRepository;
    @Override
    public Role getRoleByUserId(Long id) {
        return roleRoleRepository.getRoleByUserId(id);
    }
}
