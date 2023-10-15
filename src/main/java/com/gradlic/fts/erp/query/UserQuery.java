package com.gradlic.fts.erp.query;

public class UserQuery {
    public static final String INSERT_USER_QUERY = "INSERT INTO users(first_name, last_name, email, password) VALUES (:firstName, :lastName, :email, :password)";
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM users WHERE email= :email";

    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = "INSERT INTO account_verifications (user_id, url) VALUES (:userId, :url)";

    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = :email";

    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID = "DELETE FROM two_factor_verifications WHERE user_id = :id";
    public static final String INSERT_VERIFICATION_CODE_QUERY = "INSERT INTO two_factor_verifications (user_id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";

    public static final String SELECT_USER_BY_USER_CODE_QUERY = "SELECT * FROM users WHERE id=(SELECT user_id FROM two_factor_verifications WHERE code = :code)";

    public static final String DELETE_CODE_BY_CODE_QUERY = "DELETE FROM two_factor_verifications WHERE code= :code";

    public static final String SELECT_CODE_EXPIRATION_QUERY = "SELECT expiration_date < now() AS is_expired FROM two_factor_verifications WHERE code = :code";

    public static final String DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY = "DELETE FROM reset_password_verifications WHERE user_id= :userId";

    public static final String INSERT_PASSWORD_VERIFICATION_QUERY = "INSERT INTO reset_password_verifications(user_id, url, expiration_date) " +
            "VALUES(:userId, :url, :expirationDate)";

    public static final String SELECT_EXPIRATION_BY_URL_QUERY = "SELECT expiration_date < NOW() AS is_expired FROM reset_password_verifications WHERE url = :url";
    public static final String SELECT_USER_BY_PASSWORD_URL_QUERY = "SELECT * FROM users WHERE id = (SELECT user_id FROM reset_password_verifications WHERE url= :url)";

    public static final String UPDATE_USER_PASSWORD_BY_URL_QUERY = "UPDATE users SET password= :password WHERE id = (SELECT user_id FROM reset_password_verifications WHERE url= :url)";

    public static final String DELETE_VERIFICATION_BY_URL_QUERY = "DELETE FROM reset_password_verifications WHERE url= :url";

    public static final String SELECT_USER_BY_ACCOUNT_URL_QUERY = "SELECT * FROM users WHERE id=(SELECT user_id FROM account_verifications WHERE url = :url)";

    public static final String UPDATE_USER_ENABLED_QUERY = "UPDATE users SET is_active = :isActive WHERE id = :id";

    public static final String UPDATE_USER_DETAILS_QUERY = "UPDATE users SET first_name = :firstName, last_name= :lastName, " +
            "email= :email, mobile_number = :mobileNumber WHERE id=:id";

    public static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id=:id";

    public static final String UPDATE_USER_PASSWORD_BY_ID_QUERY = "UPDATE users SET password = :password WHERE id= :userId";

    public static final String UPDATE_USER_MFA_QUERY = "UPDATE users SET using_mfa= :isUsingMfa WHERE email= :email";

    public static final String UPDATE_USER_PROFILE_IMAGE_QUERY = "UPDATE users SET profile_image_url = :imageUrl WHERE id= :userId";

    public static final String UPDATE_USER_SETTINGS_QUERY = "UPDATE users SET is_active = :enabled, is_not_locked = :notLocked WHERE id = :userId";


}
