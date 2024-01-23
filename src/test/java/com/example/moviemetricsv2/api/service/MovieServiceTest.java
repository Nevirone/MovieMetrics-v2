package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.Genre;
import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.repository.IGenreRepository;
import com.example.moviemetricsv2.api.repository.IMovieClassificationRepository;
import com.example.moviemetricsv2.api.repository.IMovieRepository;
import com.example.moviemetricsv2.api.response.MovieResponse;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class MovieServiceTest {
    private AutoCloseable autoCloseable;

    @Mock
    private IMovieRepository movieRepository;
    @Mock
    private IMovieClassificationRepository movieClassificationRepository;
    @Mock
    private IGenreRepository genreRepository;
    private MovieService movieService;

    private Movie createMovie() {
        return Movie.builder()
                .id(1L)
                .title("Test")
                .description("Test description")
                .classification(
                        MovieClassification.builder()
                                .id(1L)
                                .name("PG")
                                .build()
                )
                .genres(new ArrayList<>())
                .build();
    }

    private MovieDto createMovieDto() {
        return MovieDto.builder()
                .title("Test")
                .description("Test description")
                .classificationId(1L)
                .genreIds(new ArrayList<>())
                .build();
    }

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        movieService = new MovieService(movieRepository, movieClassificationRepository, genreRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Create Movie: Successful no genres")
    void canAddMovie() {
        // given
        MovieDto movieDto = createMovieDto();

        given(movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(false);

        given(movieClassificationRepository.existsById(movieDto.getClassificationId()))
                .willReturn(true);

        // when
        movieService.create(movieDto);

        // then
        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieRepository).save(movieArgumentCaptor.capture());

        Movie capturedMovie = movieArgumentCaptor.getValue();

        assertThat(capturedMovie.getTitle()).isEqualTo(movieDto.getTitle());
    }

    @Test
    @DisplayName("Create Movie: Successful with genres")
    void canAddMovieWithGenres() {
        // given
        MovieDto movieDto = createMovieDto();
        movieDto.setGenreIds(List.of(1L));

        given(movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(false);

        given(movieClassificationRepository.existsById(movieDto.getClassificationId()))
                .willReturn(true);

        given(genreRepository.existsById(1L))
                .willReturn(true);

        // when
        movieService.create(movieDto);

        // then
        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieRepository).save(movieArgumentCaptor.capture());

        Movie capturedMovie = movieArgumentCaptor.getValue();

        assertThat(capturedMovie.getTitle()).isEqualTo(movieDto.getTitle());
    }

    @Test
    @DisplayName("Create Movie: Title taken")
    void addingMovieWillThrowWhenTitleIsTaken() {
        // given
        MovieDto movieDto = createMovieDto();

        given(movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> movieService.create(movieDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining(DataConflictException.titleTaken(movieDto.getTitle()).getMessage());
    }

    @Test
    @DisplayName("Create Movie: Movie classification not found")
    void addingMovieWillThrowWhenMovieClassificationIsNotFound() {
        // given
        MovieDto movieDto = createMovieDto();

        given(movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(false);

        given(movieClassificationRepository.findById(movieDto.getClassificationId()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.create(movieDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.movieClassificationNotFoundById(movieDto.getClassificationId()).getMessage());
    }

    @Test
    @DisplayName("Create Movie: Genre not found")
    void addingMovieWillThrowWhenGenreIsNotFound() {
        // given
        MovieDto movieDto = createMovieDto();
        movieDto.setGenreIds(List.of(1L));

        given(movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(false);

        given(movieClassificationRepository.existsById(movieDto.getClassificationId()))
                .willReturn(true);

        given(genreRepository.existsById(1L))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> movieService.create(movieDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.genreNotFoundById(1L).getMessage());
    }

    @Test
    @DisplayName("Get Movie: Successful")
    void canGetMovieById() {
        // given
        Long id = 2L;
        Optional<Movie> movieOptional = Optional.of(
                createMovie()
        );

        given(movieRepository.findById(id))
                .willReturn(movieOptional);

        // when
        Movie found = movieService.get(id);

        // then
        assertThat(movieOptional.get().getTitle()).isEqualTo(found.getTitle());
    }

    @Test
    @DisplayName("Get Movie: Not Found")
    void gettingMovieByIdWillThrowWhenMovieIsNotFound() {
        // given
        Long id = 2L;

        given(movieRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.get(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.movieNotFoundById(id).getMessage());
    }

    @Test
    @DisplayName("Get Movie By Title: Successful")
    void canGetMovieByTitle() {
        // given
        Optional<Movie> movieOptional = Optional.of(
                createMovie()
        );

        given(movieRepository.findByTitleIgnoreCase(movieOptional.get().getTitle()))
                .willReturn(movieOptional);

        // when
        Movie found = movieService.getByTitle(movieOptional.get().getTitle());

        // then
        assertThat(movieOptional.get().getTitle()).isEqualTo(found.getTitle());
    }

    @Test
    @DisplayName("Get Movie By Title: Not Found")
    void gettingMovieByTitleWillThrowWhenMovieIsNotFound() {
        // given
        String title = "Test";

        given(movieRepository.findByTitleIgnoreCase(title))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.getByTitle(title))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.movieNotFoundByTitle(title).getMessage());
    }

    @Test
    @DisplayName("Get All movies: Successful")
    void getAllMovies() {
        // when
        movieService.getAll();

        // then
        verify(movieRepository).findAll();
    }

    @Test
    @DisplayName("Update Movie: Successful no genres")
    void canUpdateMovie() {
        // given
        Long id = 2L;
        MovieDto movieDto = createMovieDto();

        given(movieRepository.existsById(2L))
                .willReturn(true);

        given(movieRepository.findByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(Optional.empty());

        given(movieClassificationRepository.existsById(movieDto.getClassificationId()))
                .willReturn(true);

        // when
        movieService.update(id, movieDto);

        // then
        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieRepository).existsById(id);
        verify(movieRepository).findByTitleIgnoreCase(movieDto.getTitle());

        verify(movieRepository).save(movieArgumentCaptor.capture());

        Movie capturedMovie = movieArgumentCaptor.getValue();

        assertThat(capturedMovie.getId()).isEqualTo(id);
        assertThat(capturedMovie.getTitle()).isEqualTo(movieDto.getTitle());
    }

    @Test
    @DisplayName("Update Movie: Successful with genres")
    void canUpdateMovieWithGenres() {
        // given
        Long id = 2L;
        MovieDto movieDto = createMovieDto();
        movieDto.setGenreIds(List.of(1L));

        given(movieRepository.existsById(2L))
                .willReturn(true);

        given(movieRepository.findByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(Optional.empty());

        given(movieClassificationRepository.existsById(movieDto.getClassificationId()))
                .willReturn(true);

        given(genreRepository.existsById(1L))
                .willReturn(true);

        // when
        movieService.update(id, movieDto);

        // then
        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieRepository).existsById(id);
        verify(movieRepository).findByTitleIgnoreCase(movieDto.getTitle());

        verify(movieRepository).save(movieArgumentCaptor.capture());

        Movie capturedMovie = movieArgumentCaptor.getValue();

        assertThat(capturedMovie.getId()).isEqualTo(id);
        assertThat(capturedMovie.getTitle()).isEqualTo(movieDto.getTitle());
    }

    @Test
    @DisplayName("Update Movie: Not Found")
    void updatingMovieWillThrowWhenMovieNotFound() {
        // given
        Long id = 2L;
        MovieDto movieDto = createMovieDto();

        given(movieRepository.findById(2L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.update(id, movieDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.movieNotFoundById(id).getMessage());
    }

    @Test
    @DisplayName("Update Movie: Title taken")
    void updatingMovieWillThrowWhenTitleIsTaken() {
        // given
        Long id = 2L;
        MovieDto movieDto = createMovieDto();

        given(movieRepository.existsById(2L))
                .willReturn(true);

        given(movieRepository.findByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(Optional.of(createMovie()));

        // when
        // then
        assertThatThrownBy(() -> movieService.update(id, movieDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining(DataConflictException.titleTaken(movieDto.getTitle()).getMessage());
    }

    @Test
    @DisplayName("Update Movie: Movie classification not found")
    void updatingMovieWillThrowWhenMovieClassificationNotFound() {
        // given
        Long id = 2L;
        MovieDto movieDto = createMovieDto();

        given(movieRepository.existsById(2L))
                .willReturn(true);

        given(movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(false);

        given(movieClassificationRepository.findById(movieDto.getClassificationId()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.update(id, movieDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.movieClassificationNotFoundById(movieDto.getClassificationId()).getMessage());
    }

    @Test
    @DisplayName("Update Movie: Genre not found")
    void updatingMovieWillThrowWhenGenreNotFound() {
        // given
        Long id = 2L;
        MovieDto movieDto = createMovieDto();
        movieDto.setGenreIds(List.of(1L));

        given(movieRepository.existsById(2L))
                .willReturn(true);

        given(movieRepository.findByTitleIgnoreCase(movieDto.getTitle()))
                .willReturn(Optional.empty());

        given(movieClassificationRepository.existsById(movieDto.getClassificationId()))
                .willReturn(true);

        given(genreRepository.existsById(1L))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> movieService.update(id, movieDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.genreNotFoundById(1L).getMessage());

    }

    @Test
    @DisplayName("Delete Movie: Successful")
    void canDeleteMovie() {
        // given
        Long id = 2L;

        given(movieRepository.findById(id))
                .willReturn(Optional.of(Movie.builder().build()));

        // when
        movieService.delete(id);

        // then
        verify(movieRepository).deleteById(id);
    }

    @Test
    @DisplayName("Delete Movie: Not found")
    void deletingMovieWillThrowWhenMovieNotFound() {
        // given
        Long id = 2L;

        given(movieRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NotFoundException.movieNotFoundById(id).getMessage());
    }
}