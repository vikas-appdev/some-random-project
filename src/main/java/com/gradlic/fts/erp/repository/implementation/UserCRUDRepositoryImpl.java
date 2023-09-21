package com.gradlic.fts.erp.repository.implementation;

import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.domain.Role;
import com.gradlic.fts.erp.domain.UserPrincipal;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.exception.ApiException;
import com.gradlic.fts.erp.repository.UserCRUDRepository;
import com.gradlic.fts.erp.repository.RoleRepository;
import com.gradlic.fts.erp.rolemapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

import static com.gradlic.fts.erp.enumeration.RoleType.ROLE_USER;
import static com.gradlic.fts.erp.enumeration.VerificationType.ACCOUNT;
import static com.gradlic.fts.erp.query.UserQuery.*;
import static com.gradlic.fts.erp.utils.SmsUtils.sendSMS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCRUDRepositoryImpl implements UserCRUDRepository<User>, UserDetailsService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    @Override
    public User create(User user) {
        System.out.println(getEmailCount(user.getEmail().trim().toLowerCase()));
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
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }


    private Integer getEmailCount(String email) {
        return jdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private String getVerificationUrl(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify"+type+"/"+key).toUriString();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        System.out.println(user.getFirstName());
        if (user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()).getPermission());
        }
    }
    @Override
    public User getUserByEmail(String email) {
        try{
            User user = jdbcTemplate.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            log.info("User found in database with email");
            return user;
        }catch (EmptyResultDataAccessException exception){
            throw new ApiException("No user found by email: "+email);
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);

        String verificationCode = randomNumeric(6).toUpperCase();

        try{
            jdbcTemplate.update(DELETE_VERIFICATION_CODE_BY_USER_ID, Map.of("id", user.getId()));
            jdbcTemplate.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
            sendSMS(user.getMobileNumber(), "Your six digit verification code for login into Saraiya Factory App is: "+verificationCode+"\n\n- By GRADLIC SOLUTIONS PVT LTD.");
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }
}
