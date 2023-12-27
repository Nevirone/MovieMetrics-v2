package com.example.moviemetricsv2.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserDto {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is invalid")
    @Size(max = 64, message = "Email can be at most 64 characters long")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Size(max = 32, message = "Password can be at most 32 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must contain 1 uppercase, 1 lowercase and 1 number")
    private String password;

    @NotNull(message = "IsPasswordEncrypted must be specified")
    private Boolean isPasswordEncrypted;

    @NotNull(message = "Role Id cannot be empty")
    @Min(value = 1, message = "Role Id must be valid")
    private Long roleId;
}
