package com.gradlic.fts.erp.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordForm {
    @NotEmpty(message = "Current password can not be empty")
    private String currentPassword;
    @NotEmpty(message = "New password can not be empty")
    private String newPassword;

    @NotEmpty(message = "Confirm password can not be empty")
    private String confirmNewPassword;
}
