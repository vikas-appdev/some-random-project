package com.gradlic.fts.erp.form;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {
    @NotEmpty(message = "Email can not be empty")
    @Email(message = "Invalid email, Please enter a valid email")
    private String email;
    @NotEmpty(message = "Password can not be empty")
    private String password;
}
