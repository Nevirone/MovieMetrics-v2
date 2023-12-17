package com.example.moviemetricsv2.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ERole {
    User("USER"),
    Moderator("MODERATOR"),
    Admin("ADMIN");

    private final String name;
}
