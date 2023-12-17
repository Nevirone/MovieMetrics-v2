package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.MovieMetricsV2Application;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.ERole;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.request.AuthenticationRequest;
import com.example.moviemetricsv2.api.request.RegisterRequest;
import com.example.moviemetricsv2.api.response.AuthenticationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MovieMetricsV2Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthenticationControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper objectMapper = new ObjectMapper();

    final TestRestTemplate restTemplate = new TestRestTemplate();
    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }
    private Role userRole;

    @BeforeAll
    public void setup() {
        userRole = roleRepository.findByNameIgnoreCase(ERole.User.getName())
                .orElseThrow(() -> NotFoundException.roleNotFoundByName(ERole.User.getName()));
    }
    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Register User: Successful")
    public void testCanRegisterUser() throws JsonProcessingException {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@test.com")
                .password("TestPassword1")
                .build();

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/register"),
                HttpMethod.POST, entity, String.class);

        AuthenticationResponse authenticationResponse = objectMapper.readValue(response.getBody(), AuthenticationResponse.class);
        Optional<User> found = userRepository.findByEmailIgnoreCase(registerRequest.getEmail());

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(authenticationResponse.getToken()).isNotNull();
        assertThat(found.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Register User: Bad Email")
    public void testCanRegisterUserBadEmail() {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("tes")
                .password("TestPassword1")
                .build();

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/register"),
                HttpMethod.POST, entity, String.class);

        Optional<User> found = userRepository.findByEmailIgnoreCase(registerRequest.getEmail());

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody())
                .containsIgnoringCase("email");
        assertThat(found.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Register User: Bad Password")
    public void testCanRegisterUserBadPassword() {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@test.com")
                .password("tes")
                .build();

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/register"),
                HttpMethod.POST, entity, String.class);

        Optional<User> found = userRepository.findByEmailIgnoreCase(registerRequest.getEmail());

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody())
                .containsIgnoringCase("password");
        assertThat(found.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Register User: Email Taken")
    public void testCanRegisterUserEmailTaken() {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@test.com")
                .password("TestPassword1")
                .build();

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password("AnyPassword1")
                .role(userRole)
                .build();

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest);

        userRepository.save(user);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/register"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getBody())
                .containsIgnoringCase("email")
                .contains(registerRequest.getEmail())
                .containsIgnoringCase("taken");
    }

    @Test
    @DisplayName("Login User: Successful")
    public void testCanLogin() throws JsonProcessingException {
        // given
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("test@test.com")
                .password("TestPassword1")
                .build();

        User user = User.builder()
                .email(authenticationRequest.getEmail())
                .password(passwordEncoder.encode(authenticationRequest.getPassword()))
                .role(userRole)
                .build();

        HttpEntity<AuthenticationRequest> entity = new HttpEntity<>(authenticationRequest);

        userRepository.save(user);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, entity, String.class);

        AuthenticationResponse authenticationResponse = objectMapper.readValue(response.getBody(), AuthenticationResponse.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(authenticationResponse.getToken()).isNotNull();
    }

    @Test
    @DisplayName("Login User: Bad Email")
    public void testCanLoginBadEmail() throws JsonProcessingException {
        // given
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("test@test.com")
                .password("TestPassword1")
                .build();

        HttpEntity<AuthenticationRequest> entity = new HttpEntity<>(authenticationRequest);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, entity, String.class);

        AuthenticationResponse authenticationResponse = objectMapper.readValue(response.getBody(), AuthenticationResponse.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(authenticationResponse.getToken()).isNull();
        assertThat(authenticationResponse.getMessage()).isNotNull();
    }

    @Test
    @DisplayName("Login User: Bad Password")
    public void testCanLoginBadPassword() throws JsonProcessingException {
        // given
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("test@test.com")
                .password("TestPassword1")
                .build();

        User user = User.builder()
                .email(authenticationRequest.getEmail())
                .password(passwordEncoder.encode("AnyPassword1"))
                .role(userRole)
                .build();

        HttpEntity<AuthenticationRequest> entity = new HttpEntity<>(authenticationRequest);

        userRepository.save(user);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, entity, String.class);

        AuthenticationResponse authenticationResponse = objectMapper.readValue(response.getBody(), AuthenticationResponse.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(authenticationResponse.getToken()).isNull();
        assertThat(authenticationResponse.getMessage()).isNotNull();
    }
}
