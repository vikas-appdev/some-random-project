package com.gradlic.fts.erp.enumeration;

public enum EventType {
    LOGIN_ATTEMPT("You tried to log in"),
    LOGIN_ATTEMPT_FAILURE("You tried to login and it failed"),
    LOGIN_ATTEMPT_SUCCESS("You tried to log in and it success"),
    PROFILE_UPDATE("You updated your profile information"),
    PROFILE_PICTURE_UPDATE("You updated your profile picture"),
    ROLE_UPDATE("You updated your role and permissions"),
    ACCOUNT_SETTINGS_UPDATE("You updated your account settings"),
    PASSWORD_UPDATE("You updated your password"),
    MFA_UPDATE("You updated your MFA settings");

    private final String description;

    EventType(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
