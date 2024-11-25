package com.uofc.acmeplex.dto.request.auth;

import com.uofc.acmeplex.enums.UserTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSignUpDto {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
    private String email;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "User Type is required")
    private UserTypeEnum userType;
}
