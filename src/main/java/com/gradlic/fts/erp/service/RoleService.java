package com.gradlic.fts.erp.service;

import com.gradlic.fts.erp.domain.Role;

import java.util.Collection;

public interface RoleService {
    Role getRoleByUserId(Long id);
    Collection<Role> getRoles();
}
