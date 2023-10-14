package com.gradlic.fts.erp.repository.implementation;

import com.gradlic.fts.erp.domain.Role;
import com.gradlic.fts.erp.domain.User;
import com.gradlic.fts.erp.domain.UserPrincipal;
import com.gradlic.fts.erp.dto.UserDTO;
import com.gradlic.fts.erp.enumeration.VerificationType;
import com.gradlic.fts.erp.exception.ApiException;
import com.gradlic.fts.erp.form.UpdateForm;
import com.gradlic.fts.erp.repository.RoleRepository;
import com.gradlic.fts.erp.repository.UserCRUDRepository;
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
import static com.gradlic.fts.erp.enumeration.VerificationType.PASSWORD;
import static com.gradlic.fts.erp.query.UserQuery.*;
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
        try{
            return jdbcTemplate.queryForObject(SELECT_USER_BY_ID, Map.of("id", id), new UserRowMapper());
        }catch (EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw new ApiException("No user found by given id: "+id);
        } catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred please try again");
        }
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

    private SqlParameterSource getUserDetailsSqlParameterSource(UpdateForm user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("mobileNumber", user.getMobileNumber());
    }

    private String getVerificationUrl(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/"+type+"/"+key).toUriString();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if (user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
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
            // sendSMS(user.getMobileNumber(), "Your six digit verification code for login into Saraiya Factory App is: "+verificationCode+"\n\n- By GRADLIC SOLUTIONS PVT LTD.");
            log.info("Verification Code is: {}", verificationCode);
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User verifyCode(String email, String code) {
        if (isVerificationCodeExpired(code)) throw new ApiException("This code has expired. Please login again.");
        try{
            User userByCode = jdbcTemplate.queryForObject(SELECT_USER_BY_USER_CODE_QUERY, Map.of("code", code), new UserRowMapper());
            User userByEmail = jdbcTemplate.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            if (userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())){
                jdbcTemplate.update(DELETE_CODE_BY_CODE_QUERY, Map.of("code", code));
                return userByCode;
            }else {
                throw new ApiException("Code is invalid. Please try again.");
            }
        }catch(EmptyResultDataAccessException exception){
            throw new ApiException("Unable to find record");
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }

    @Override
    public void resetPassword(String email) {
        if(getEmailCount(email.trim().toLowerCase()) <=0) throw new ApiException("No account found for the given email address");
        try{
            String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
            User user = getUserByEmail(email);
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
            jdbcTemplate.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, Map.of("userId", user.getId()));
            jdbcTemplate.update(INSERT_PASSWORD_VERIFICATION_QUERY, Map.of("userId", user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
            // Send email with url to the user
            log.info("Verification Url: {}", verificationUrl);
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        if (isLinkExpired(key, PASSWORD)) throw new ApiException("This link has expired. Please reset your password again.");
        try{
            User user = jdbcTemplate.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
            // DELETE USER FROM PASSWORD VERIFICATION TABLE
            return user;
        }catch(EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid, Please reset your password again.");
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) throw new ApiException("Password don't match, Please try again");
        try{
            jdbcTemplate.update(UPDATE_USER_PASSWORD_BY_URL_QUERY, Map.of("password", encoder.encode(password), "url", getVerificationUrl(key, PASSWORD.getType())));
            jdbcTemplate.update(DELETE_VERIFICATION_BY_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())));
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }

    @Override
    public User verifyAccountKey(String key) {
        try{
            User user =  jdbcTemplate.queryForObject(SELECT_USER_BY_ACCOUNT_URL_QUERY, Map.of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
            jdbcTemplate.update(UPDATE_USER_ENABLED_QUERY, Map.of("isActive", true, "id", user.getId()));
            // DELETE AFTER UPDATING
            return user;
        }catch(EmptyResultDataAccessException exception){
            throw new ApiException("This link is not valid.");
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }

    @Override
    public User updateUserDetails(UpdateForm user) {
        try{
            jdbcTemplate.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(user));
            return get(user.getId());
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
        if (!newPassword.equals(confirmNewPassword)) throw new ApiException("New password does not match with confirm password, Please try again.");
        User user = get(id);
        if (encoder.matches(currentPassword, user.getPassword())){
            try{
                jdbcTemplate.update(UPDATE_USER_PASSWORD_BY_ID_QUERY, Map.of("userId", id, "password", encoder.encode(newPassword)));
            }catch(Exception exception){
                throw new ApiException("An error occurred, Please try again.");
            }
        }else {
            throw new ApiException("Incorrect current password, Please try again");
        }

    }

    @Override
    public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
        try{
            jdbcTemplate.update(UPDATE_USER_SETTINGS_QUERY, Map.of("userId", userId, "enabled", enabled, "notLocked", notLocked));
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }

    private Boolean isLinkExpired(String key, VerificationType password) {
        try{
            return jdbcTemplate.queryForObject(SELECT_EXPIRATION_BY_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())), Boolean.class);
        }catch(EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid, Please reset your password again.");
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }

    private Boolean isVerificationCodeExpired(String code) {
        try{
            return jdbcTemplate.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code), Boolean.class);
        }catch(EmptyResultDataAccessException exception){
            throw new ApiException("This code is not valid. Please login again.");
        }catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again.");
        }
    }
}
