package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.MovieMetricsV2Application;
import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.*;
import com.example.moviemetricsv2.api.repository.IMovieClassificationRepository;
import com.example.moviemetricsv2.api.repository.IMovieRepository;
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
public class MovieControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    IMovieRepository movieRepository;
    @Autowired
    IMovieClassificationRepository movieClassificationRepository;

    final TestRestTemplate restTemplate = new TestRestTemplate();
    final ObjectMapper objectMapper = new ObjectMapper();

    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }

    private final HttpHeaders userHeaders = new HttpHeaders();
    private final HttpHeaders moderatorHeaders = new HttpHeaders();
    private final HttpHeaders adminHeaders = new HttpHeaders();

    private MovieClassification movieClassification;

    private Movie createMovie(String title) {
        return movieRepository.save(
                Movie.builder()
                        .title(title)
                        .description("Test description")
                        .classification(movieClassification)
                        .build()
        );
    }

    private MovieDto createMovieDto(String title) {
        return MovieDto.builder()
                .title(title)
                .description("Test description")
                .classificationId(movieClassification.getId())
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
        movieClassification = movieClassificationRepository.findByNameIgnoreCase(EMovieClassification.GeneralAudience.getName())
                .orElseThrow(() -> NotFoundException.movieClassificationNotFoundByName(EMovieClassification.GeneralAudience.getName()));

        Role userRole = roleRepository.findByNameIgnoreCase(ERole.User.getName())
                .orElseThrow(() -> new RuntimeException(NotFoundException.roleNotFoundByName(ERole.User.getName())));

        Role moderatorRole = roleRepository.findByNameIgnoreCase(ERole.Moderator.getName())
                .orElseThrow(() -> new RuntimeException(NotFoundException.roleNotFoundByName(ERole.Moderator.getName())));

        Role adminRole = roleRepository.findByNameIgnoreCase(ERole.Admin.getName())
                .orElseThrow(() -> new RuntimeException(NotFoundException.roleNotFoundByName(ERole.Admin.getName())));

        userHeaders.setBearerAuth(createUserAndLogin("user@user.com", userRole));
        moderatorHeaders.setBearerAuth(createUserAndLogin("moderator@moderator.com", moderatorRole));
        adminHeaders.setBearerAuth(createUserAndLogin("admin@admin.com", adminRole));
    }

    @BeforeEach
    public void cleanUp() {
        movieRepository.deleteAll();
    }

    @Test
    @DisplayName("Create Movie: Successful as Moderator")
    public void testPostMovieAsModerator() throws JsonProcessingException {
        // given
        MovieDto movieDto = createMovieDto("Test");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.CREATED.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(movieDto.getTitle());
    }

    @Test
    @DisplayName("Create Movie: Successful as Admin")
    public void testPostMovieAsAdmin() throws JsonProcessingException {
        // given
        MovieDto movieDto = createMovieDto("Test");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.CREATED.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(movieDto.getTitle());
    }

    @Test
    @DisplayName("Create Movie: Not Authenticated")
    public void testPostMovieNotAuthenticated() {
        // given
        MovieDto movieDto = createMovieDto("Test");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Create Movie: No permission role User")
    public void testPostMovieNoPermissionRoleUser() {
        // given
        MovieDto movieDto = createMovieDto("Test");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Create Movie: Title Taken")
    public void testPostMovieTakenTitle() {
        // given
        MovieDto movieDto = createMovieDto("Test");

        createMovie(movieDto.getTitle());

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getBody()).contains("Title " + movieDto.getTitle() + " is taken");
    }

    @Test
    @DisplayName("Create Movie: Invalid Title")
    public void testPostMovieBadTitle() {
        // given
        MovieDto movieDto = createMovieDto("te");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody()).containsIgnoringCase("Title");
    }

    @Test
    @DisplayName("Update Movie: Successful as Moderator")
    public void testPatchMovieAsModerator() throws JsonProcessingException {
        // given
        MovieDto movieDto = createMovieDto("Test");

        Movie saved = createMovie("TestMe");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(movieDto.getTitle());
    }

    @Test
    @DisplayName("Update Movie: Successful as Admin")
    public void testPatchMovieAsAdmin() throws JsonProcessingException {
        // given
        MovieDto movieDto = createMovieDto("Test");

        Movie saved = createMovie("TestMe");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(movieDto.getTitle());
    }

    @Test
    @DisplayName("Update Movie: Not Authenticated")
    public void testPatchMovieNotAuthenticated() {
        // given
        MovieDto movieDto = createMovieDto("Test");

        Movie saved = createMovie("TestMe");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Update Movie: No permission role User")
    public void testPatchMovieNoPermissionRoleUser() {
        // given
        MovieDto movieDto = createMovieDto("Test");

        Movie saved = createMovie("TestMe");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Get Movie: Successful role Admin")
    public void testGetMovieAsAdmin() throws JsonProcessingException {
        // given
        Movie saved = createMovie("Test");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(saved.getTitle());
    }

    @Test
    @DisplayName("Get Movie: Successful role Moderator")
    public void testGetMovieAsModerator() throws JsonProcessingException {
        // given
        Movie saved = createMovie("Test");

        HttpEntity<String> entity = new HttpEntity<>(null, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(saved.getTitle());
    }

    @Test
    @DisplayName("Get Movie: Successful role User")
    public void testGetMovieAsUser() throws JsonProcessingException {
        // given
        Movie saved = createMovie("Test");

        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(saved.getTitle());
    }

    @Test
    @DisplayName("Get Movie: Not Authenticated")
    public void testGetMovieNotAuthenticated() {
        // given
        Movie saved = createMovie("Test");

        HttpEntity<String> entity = new HttpEntity<>(null);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Get Movie: Not Found")
    public void testGetMovieByNotFound() {
        // given
        long id = 12L;
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + id),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody()).contains("Movie with id " + id + " not found");
    }

    @Test
    @DisplayName("Delete Movie: Successful as Admin")
    public void testDeleteMovieAsAdmin() throws JsonProcessingException {
        // given
        Movie saved = createMovie("Test");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(saved.getTitle());
    }

    @Test
    @DisplayName("Delete Movie: Successful as Moderator")
    public void testDeleteMovieAsModerator() throws JsonProcessingException {
        // given
        Movie saved = createMovie("Test");

        HttpEntity<String> entity = new HttpEntity<>(null, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Movie movie = objectMapper.readValue(response.getBody(), Movie.class);

        assertThat(movie.getTitle()).isEqualTo(saved.getTitle());
    }

    @Test
    @DisplayName("Delete Movie: Not Authenticated")
    public void testDeleteMovieNotAuthenticated() {
        // given
        Movie saved = createMovie("Test");

        HttpEntity<String> entity = new HttpEntity<>(null);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Delete Movie: No permission role User")
    public void testDeleteMovieNoPermissionRoleUser() {
        // given
        Movie saved = createMovie("Test");

        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Delete Movie: Not Found")
    public void testDeleteMovieNotFound() {
        // given
        long id = 12L;
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + id),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody()).contains("Movie with id " + id + " not found");
    }

    @Test
    @DisplayName("Get All Movies: Successful as Admin")
    public void testGetMovies() throws JsonProcessingException {
        // given
        createMovie("Test1");
        createMovie("Test2");
        createMovie("Test3");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        List<Movie> movies = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        assertThat(movies.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Get All Movies: Successful as Moderator")
    public void testGetMoviesAsModerator() throws JsonProcessingException {
        // given
        createMovie("Test1");
        createMovie("Test2");
        createMovie("Test3");

        HttpEntity<String> entity = new HttpEntity<>(null, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        List<Movie> movies = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        assertThat(movies.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Get All Movies: Successful as User")
    public void testGetMoviesAsUser() throws JsonProcessingException {
        // given
        createMovie("Test1");
        createMovie("Test2");
        createMovie("Test3");

        HttpEntity<String> entity = new HttpEntity<>(null, moderatorHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        List<Movie> movies = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        assertThat(movies.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Get All Movies: Not Authenticated")
    public void testGetMoviesNotAuthenticated() {
        // given
        createMovie("Test1");
        createMovie("Test2");
        createMovie("Test3");

        HttpEntity<String> entity = new HttpEntity<>(null);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
