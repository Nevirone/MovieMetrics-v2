package com.example.moviemetricsv2.api.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AuthenticationResponse {
    private String token;
    private String message;
}
