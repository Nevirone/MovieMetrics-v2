package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.MovieMetricsV2Application;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.request.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
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

    final TestRestTemplate restTemplate = new TestRestTemplate();
    final HttpHeaders headers = new HttpHeaders();
    final ObjectMapper objectMapper = new ObjectMapper();

    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }

    private User createUser(String email) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .password("Testpas1")
                        .build()
        );
    }

    private UserDto createUserDto(String email, String password) {
        return UserDto.builder()
                .email(email)
                .password(password)
                .build();
    }

    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create User: Successful")
    public void testPostUser() throws JsonProcessingException {
        // given
        UserDto userDto = createUserDto("test@test.com", "Testpass1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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
    @DisplayName("Create User: Email Taken")
    public void testPostUserTakenTitle() {
        // given
        UserDto userDto = createUserDto("test@test.com", "Testpass1");

        createUser(userDto.getEmail());

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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
        UserDto userDto = createUserDto("test", "Testpass1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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
        UserDto userDto = createUserDto("test@test.com", "Testpass1");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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
    @DisplayName("Update User: Email Taken")
    public void testPatchUserTakenTitle() {
        // given
        UserDto userDto = createUserDto("test@test.com", "Testpass1");

        createUser(userDto.getEmail());

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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
        UserDto userDto = createUserDto("tes", "Testpass1");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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
        Long id = 12L;
        UserDto userDto = createUserDto("test@test.com", "Testpass1");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

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
    @DisplayName("Get User: Successful")
    public void testGetUserById() throws JsonProcessingException{
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

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
    @DisplayName("Get User: Not Found")
    public void testGetUserByNotFound() {
        // given
        Long id = 12L;
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

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

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

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
    @DisplayName("Delete User: Not Found")
    public void testDeleteUserNotFound() {
        // given
        Long id = 12L;
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

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
    public void testGetUsers() throws JsonProcessingException{
        // given
        User user1 = createUser("test1@test.com");
        User user2 = createUser("test2@test.com");
        User user3 = createUser("test3@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());


        List<User> users = objectMapper.readValue(response.getBody(), new TypeReference<List<User>>(){});

        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0).getEmail()).isEqualTo(user1.getEmail());
        assertThat(users.get(1).getEmail()).isEqualTo(user2.getEmail());
        assertThat(users.get(2).getEmail()).isEqualTo(user3.getEmail());

    }

}
