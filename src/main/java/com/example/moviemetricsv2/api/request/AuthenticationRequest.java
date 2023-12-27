package com.example.moviemetricsv2.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AuthenticationRequest {
    @NotBlank(message = "Email cannot be empty")
    @Size(max = 64, message = "Email can be at most 64 characters long")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
