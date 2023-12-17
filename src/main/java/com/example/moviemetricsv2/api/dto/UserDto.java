package com.example.moviemetricsv2.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserDto {
    @Email(message = "Email is invalid")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must contain 1 uppercase, 1 lowercase and 1 number")
    private String password;

    @NotNull(message = "IsPasswordEncrypted must be specified")
    private Boolean isPasswordEncrypted;

    @NotBlank(message = "Role cannot be empty")
    private String role;
}
