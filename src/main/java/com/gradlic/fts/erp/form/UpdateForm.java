package com.gradlic.fts.erp.form;

import com.gradlic.fts.erp.domain.Address;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateForm {
    @NotNull(message = "Id can not be null or empty")
    private Long id;

    @NotNull(message = "Firstname can not be null")
    private String firstName;

    @NotNull(message = "Last name can not be null")
    private String lastName;
    @NotNull(message = "Email can not be null")
    @Email(message = "Invalid email, Please enter a valid email")
    private String email; // UNIQUE
    private String aadhaarNumber;
    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    private String mobileNumber;
    private String title;
    private String bio;

    @Embedded
    private Address address;

}
