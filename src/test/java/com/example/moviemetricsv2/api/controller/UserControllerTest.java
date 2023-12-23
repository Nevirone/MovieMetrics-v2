package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.MovieMetricsV2Application;
import com.example.moviemetricsv2.api.dto.UserDto;
import com.example.moviemetricsv2.api.model.ERole;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.request.AuthenticationRequest;
import com.example.moviemetricsv2.api.response.AuthenticationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MovieMetricsV2Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    final TestRestTemplate restTemplate = new TestRestTemplate();
    final ObjectMapper objectMapper = new ObjectMapper();

    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }

    private Role userRole;

    private final HttpHeaders userHeaders = new HttpHeaders();
    private final HttpHeaders moderatorHeaders = new HttpHeaders();
    private final HttpHeaders adminHeaders = new HttpHeaders();

    private User createUser(String email) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .password("TestPassword1")
                        .role(userRole)
                        .build()
        );
    }

    private UserDto createUserDto(String email, String password) {
        return UserDto.builder()
                .email(email)
                .password(password)
                .isPasswordEncrypted(true)
                .roleId(1L)
                .build();
    }

    private String createUserAndLogin(String email, Role role) throws JsonProcessingException {
        String password = "TestPassword1";
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        userRepository.save(user);

        HttpEntity<AuthenticationRequest> entity = new HttpEntity<>(
                AuthenticationRequest.builder()
                        .email(email)
                        .password(password)
                        .build()
        );

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, entity, String.class);

        AuthenticationResponse authenticationResponse = objectMapper.readValue(response.getBody(), AuthenticationResponse.class);
        if (authenticationResponse.getToken() == null) throw new RuntimeException("Login error");

        return authenticationResponse.getToken();
    }

    @BeforeAll
    public void setup() throws JsonProcessingException {
        userRole = roleRepository.findByNameIgnoreCase(ERole.User.getName())
                .orElseThrow(() -> new RuntimeException("Role not found " + ERole.User.getName()));

        Role moderatorRole = roleRepository.findByNameIgnoreCase(ERole.Moderator.getName())
                .orElseThrow(() -> new RuntimeException("Role not found " + ERole.Moderator.getName()));

        Role adminRole = roleRepository.findByNameIgnoreCase(ERole.Admin.getName())
                .orElseThrow(() -> new RuntimeException("Role not found " + ERole.Admin.getName()));

        userHeaders.setBearerAuth(createUserAndLogin("user@user.com", userRole));
        moderatorHeaders.setBearerAuth(createUserAndLogin("moderator@moderator.com", moderatorRole));
        adminHeaders.setBearerAuth(createUserAndLogin("admin@admin.com", adminRole));
    }

    @BeforeEach
    public void cleanUp() {
        List<User> users = userRepository.findAll();
        for (User user : users)
            if (!List.of("user@user.com", "moderator@moderator.com", "admin@admin.com").contains(user.getEmail()))
                userRepository.delete(user);
    }

    @Test
    @DisplayName("Create User: Successful")
    public void testPostUser() throws JsonProcessingException {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.CREATED.value());

        User user = objectMapper.readValue(response.getBody(), User.class);

        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @DisplayName("Create User: Not Authenticated")
    public void testPostUserNotAuthenticated() {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Create User: No permission role User")
    public void testPostUserNoPermissionRoleUser() {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Create User: No permission role Moderator")
    public void testPostUserNoPermissionRoleModerator() {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Create User: Email Taken")
    public void testPostUserTakenEmail() {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        createUser(userDto.getEmail());

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getBody()).contains("Email " + userDto.getEmail() + " is taken");
    }

    @Test
    @DisplayName("Create User: Invalid Email")
    public void testPostUserBadEmail() {
        // given
        UserDto userDto = createUserDto("test", "TestPassword1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody()).containsIgnoringCase("Email");
    }

    @Test
    @DisplayName("Create User: Invalid Password")
    public void testPostUserBadPassword() {
        // given
        UserDto userDto = createUserDto("test@test.com", "tes");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody()).containsIgnoringCase("Password");
    }

    @Test
    @DisplayName("Update User: Successful")
    public void testPatchUser() throws JsonProcessingException {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        User user = objectMapper.readValue(response.getBody(), User.class);

        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @DisplayName("Update User: Not Authenticated")
    public void testPatchUserNotAuthenticated() {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Update User: No permission role User")
    public void testPatchUserNoPermissionRoleUser() {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Update User: No permission role Moderator")
    public void testPatchUserNoPermissionRoleModerator() {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Update User: Email Taken")
    public void testPatchUserTakenEmail() {
        // given
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        createUser(userDto.getEmail());

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getBody()).contains("Email " + userDto.getEmail() + " is taken");
    }

    @Test
    @DisplayName("Update User: Invalid Email")
    public void testPatchUserBadEmail() {
        // given
        UserDto userDto = createUserDto("tes", "TestPassword1");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody()).containsIgnoringCase("Email");
    }

    @Test
    @DisplayName("Update User: Invalid Password")
    public void testPatchUserBadPassword() {
        // given
        UserDto userDto = createUserDto("test@test.com", "tes");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody()).containsIgnoringCase("Password");
    }

    @Test
    @DisplayName("Update User: Not Found")
    public void testPatchUserNotFound() {
        // given
        long id = 12L;
        UserDto userDto = createUserDto("test@test.com", "TestPassword1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + id),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody()).contains("User with id " + id + " not found");
    }

    @Test
    @DisplayName("Get User: Successful role Admin")
    public void testGetUserById() throws JsonProcessingException {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        User user = objectMapper.readValue(response.getBody(), User.class);

        assertThat(user.getEmail()).isEqualTo(saved.getEmail());
    }

    @Test
    @DisplayName("Get User: Successful role Moderator")
    public void testGetUserByIdWithRoleModerator() throws JsonProcessingException {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        User user = objectMapper.readValue(response.getBody(), User.class);

        assertThat(user.getEmail()).isEqualTo(saved.getEmail());
    }

    @Test
    @DisplayName("Get User: Not Authenticated")
    public void testGetUserNotAuthenticated() {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Get User: No permission role User")
    public void testGetUserByIdNoPermissionRoleUser() {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Get User: Not Found")
    public void testGetUserByNotFound() {
        // given
        long id = 12L;
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + id),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody()).contains("User with id " + id + " not found");
    }

    @Test
    @DisplayName("Delete User: Successful")
    public void testDeleteUser() throws JsonProcessingException {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        User user = objectMapper.readValue(response.getBody(), User.class);

        assertThat(user.getEmail()).isEqualTo(saved.getEmail());
    }

    @Test
    @DisplayName("Delete User: Not Authenticated")
    public void testDeleteUserNotAuthenticated() {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Delete User: No permission role User")
    public void testDeleteUserNoPermissionRoleUser() {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Delete User: No permission role Moderator")
    public void testDeleteUserNoPermissionRoleModerator() {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Delete User: Not Found")
    public void testDeleteUserNotFound() {
        // given
        long id = 12L;
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + id),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody()).contains("User with id " + id + " not found");
    }

    @Test
    @DisplayName("Get All Users: Successful")
    public void testGetUsers() throws JsonProcessingException {
        // given
        createUser("test1@test.com");
        createUser("test2@test.com");
        createUser("test3@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        assertThat(users.size()).isEqualTo(6); // 3 created and 3 for testing permissions
    }

    @Test
    @DisplayName("Get All Users: Successful role Moderator")
    public void testGetUsersWithRoleModerator() throws JsonProcessingException {
        // given
        createUser("test1@test.com");
        createUser("test2@test.com");
        createUser("test3@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        assertThat(users.size()).isEqualTo(6); // 3 created and 3 for testing permissions
    }

    @Test
    @DisplayName("Get All Users: Not Authenticated")
    public void testGetUsersNotAuthenticated() {
        // given
        createUser("test1@test.com");
        createUser("test2@test.com");
        createUser("test3@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Get All Users: No permission role User")
    public void testGetUsersNoPermissionRoleUser() {
        // given
        createUser("test1@test.com");
        createUser("test2@test.com");
        createUser("test3@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
}
