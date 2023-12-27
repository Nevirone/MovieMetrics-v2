package com.example.moviemetricsv2.api.response;

import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String role;

    public UserResponse(User user) {
        id = user.getId();
        email = user.getEmail();
        role = user.getRole().getName();
    }
}
