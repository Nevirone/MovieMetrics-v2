package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.InternalServerException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.ERole;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.request.AuthenticationRequest;
import com.example.moviemetricsv2.api.request.RegisterRequest;
import com.example.moviemetricsv2.api.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegisterRequest registerRequest) throws DataConflictException {
        Optional<User> found = userRepository.findByEmail(registerRequest.getEmail());

        if (found.isPresent()) throw DataConflictException.emailTaken(registerRequest.getEmail());

        Optional<Role> userRole = roleRepository.findByName(ERole.USER.toString());
        if (userRole.isEmpty())
            throw new InternalServerException("Role " + ERole.USER.toString() + " not found");

        User user =  userRepository.save(
                User.builder()
                        .email(registerRequest.getEmail())
                        .password(passwordEncoder.encode(registerRequest.getPassword()))
                        .role(userRole.get())
                        .build()
        );

        String jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwt).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws NotFoundException {
        Optional<User> user = userRepository.findByEmail(authenticationRequest.getEmail());

        if (user.isEmpty()) throw NotFoundException.userNotFoundByEmail(authenticationRequest.getEmail());

        String jwt = jwtService.generateToken(user.get());
        return AuthenticationResponse.builder().token(jwt).build();
    }
}
