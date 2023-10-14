package com.gradlic.fts.erp.query;

public class RoleQuery {

    public static final String SELECT_ROLES_QUERY = "SELECT * FROM roles ORDER BY id";

    public static final String UPDATE_USER_ROLE_QUERY = "UPDATE user_roles SET role_id = :roleId WHERE user_id= :userId";
    public static final String INSERT_ROLE_TO_USER = "INSERT INTO user_roles(user_id, role_id) VALUES (:userId, :roleId)";
    public static final String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM roles WHERE name = :name";

    public static final String SELECT_ROLE_BY_ID_QUERY = "SELECT r.id, r.name, r.permission FROM roles r join user_roles ur ON ur.role_id = r.id JOIN users u ON u.id = ur.user_id WHERE u.id = :id";
}
