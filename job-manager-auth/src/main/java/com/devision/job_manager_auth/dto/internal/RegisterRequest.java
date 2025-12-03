package com.devision.job_manager_auth.dto.internal;

import com.devision.job_manager_auth.entity.Country;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be less than 200 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must have at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least 1 number, 1 uppercase letter, and 1 special character"
    )
    private String password;

    @NotNull(message = "Country is required")
    private Country country;

    @Pattern(
            regexp = "^\\+\\d{1,3}\\d{1,13}$",
            message = "Phone must start with country code (e.g., +84) and contain max 13 digits after code"
    )
    private String phone;

    @Size(max = 255, message = "Street name is too long")
    private String street;

    @Size(max = 100, message = "City name is too long")
    private String city;

    @Size(max = 255, message = "Company's name is too long")
    private String name;
}
