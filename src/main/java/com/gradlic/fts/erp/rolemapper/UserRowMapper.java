package com.gradlic.fts.erp.rolemapper;

import com.gradlic.fts.erp.domain.Role;
import com.gradlic.fts.erp.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .aadhaarNumber(rs.getString("aadhaar_number"))
                .title(rs.getString("title"))
                .bio(rs.getString("bio"))
                .isActive(rs.getBoolean("is_active"))
                .isNotLocked(rs.getBoolean("is_not_locked"))
                .isUsingMFA(rs.getBoolean("using_mfa"))
                //.lastLoginDate(rs.getTimestamp("last_login_date").toLocalDateTime())
                //.lastLoginDateDisplay(rs.getTimestamp("last_login_date_display").toLocalDateTime())

                .build();
    }
}
