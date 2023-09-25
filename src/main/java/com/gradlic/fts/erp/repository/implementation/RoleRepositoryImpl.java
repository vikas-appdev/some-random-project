package com.gradlic.fts.erp.repository.implementation;

import com.gradlic.fts.erp.domain.Role;
import com.gradlic.fts.erp.exception.ApiException;
import com.gradlic.fts.erp.repository.RoleRepository;
import com.gradlic.fts.erp.rolemapper.RoleRowMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.gradlic.fts.erp.enumeration.RoleType.ROLE_USER;
import static com.gradlic.fts.erp.query.RoleQuery.*;
import static java.util.Objects.*;

@Repository
@AllArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Adding role {} to user id: {}", roleName, userId);
        try{
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("name", roleName), new RoleRowMapper());
            jdbc.update(INSERT_ROLE_TO_USER, Map.of("userId", userId, "roleId", requireNonNull(role).getId()));

        }catch (EmptyResultDataAccessException exception){
            throw new ApiException("No role found by name" + ROLE_USER.name());
        }catch(Exception exception){
            throw new ApiException("An error occurred. Please try again");
        }

    }

    @Override
    public Role getRoleByUserId(Long userId) {

        try{
            log.info("fetch role to user id: {}", userId);
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_ID_QUERY, Map.of("id", userId), new RoleRowMapper());
            return role;
        }catch (EmptyResultDataAccessException exception){
            throw new ApiException("No role found by name" + ROLE_USER.name());
        }catch(Exception exception){
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
