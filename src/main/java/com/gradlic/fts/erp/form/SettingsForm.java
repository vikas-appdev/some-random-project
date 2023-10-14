package com.gradlic.fts.erp.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsForm {
    @NotNull(message = "Enabled can not be null or empty")
    private Boolean active;

    @NotNull(message = "Not locked can not be null or empty")
    private Boolean notLocked;
}
