package com.gradlic.fts.erp.repository.implementation;

import com.gradlic.fts.erp.domain.Person;
import com.gradlic.fts.erp.domain.Role;
import com.gradlic.fts.erp.exception.ApiException;
import com.gradlic.fts.erp.repository.PersonCRUDRepository;
import com.gradlic.fts.erp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.gradlic.fts.erp.enumeration.RoleType.ROLE_USER;
import static com.gradlic.fts.erp.enumeration.VerificationType.ACCOUNT;
import static com.gradlic.fts.erp.query.UserQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PersonCRUDRepositoryImpl implements PersonCRUDRepository<Person> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Person create(Person user) {
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0 ) throw new ApiException("Email already in use. Please use a different email and try again.");
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            SqlParameterSource source = getSqlParameterSource(user);
            jdbcTemplate.update(INSERT_USER_QUERY, source, keyHolder);
            user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            jdbcTemplate.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));
            // emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setActive(false);
            user.setNotLocked(true);
            return user;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }




    @Override
    public Collection<Person> list(int page, int pageSize) {
        return null;
    }

    @Override
    public Person get(Long id) {
        return null;
    }

    @Override
    public Person update(Person data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }


    private Integer getEmailCount(String email) {
        return jdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }

    private SqlParameterSource getSqlParameterSource(Person user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private String getVerificationUrl(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify"+type+"/"+key).toUriString();
    }
}
