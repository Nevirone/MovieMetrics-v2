package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.MovieClassification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IMovieRepositoryTest {
    @Autowired
    IMovieRepository movieRepository;
    @Autowired
    IMovieClassificationRepository movieClassificationRepository;

    @Test
    @DisplayName("Find Movie By Name: Successful")
    void testCanFindMovieByName() {
        // given
        String title = "Saw";
        MovieClassification classification = movieClassificationRepository.save(
                MovieClassification.builder().id(1L).name("PG").build()
        );
        Movie movie = Movie.builder().title(title).description("test").classification(classification).build();

        movieRepository.save(movie);

        // when
        Optional<Movie> found = movieRepository.findByTitleIgnoreCase(title);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getTitle()).isEqualTo(movie.getTitle());
    }

    @Test
    @DisplayName("Find Movie By Name Case Insensitive: Successful")
    void testCanFindMovieByNameCaseInsensitive() {
        // given
        String title = "Saw";
        MovieClassification classification = movieClassificationRepository.save(
                MovieClassification.builder().id(1L).name("PG").build()
        );
        Movie movie = Movie.builder().title(title.toLowerCase()).description("test").classification(classification).build();

        movieRepository.save(movie);

        // when
        Optional<Movie> found = movieRepository.findByTitleIgnoreCase(title);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getTitle()).isEqualTo(movie.getTitle());
    }

    @Test
    @DisplayName("Find Movie By Name: Not Found")
    void testCanFindByNameIfMovieDoesNotExist() {
        // given
        String title = "Saw";

        // when
        Optional<Movie> found = movieRepository.findByTitleIgnoreCase(title);

        // then
        assertThat(found.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Check Name Exists: Successful")
    void testCanCheckNameExists() {
        // given
        String title = "Saw";
        MovieClassification classification = movieClassificationRepository.save(
                MovieClassification.builder().id(1L).name("PG").build()
        );
        Movie movie = Movie.builder().title(title).description("test").classification(classification).build();

        movieRepository.save(movie);

        // when
        // then
        assertThat(movieRepository.existsByTitleIgnoreCase(title)).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists Case Insensitive: Successful")
    void testCanCheckNameExistsCaseInsensitive() {
        // given
        String title = "Saw";
        MovieClassification classification = movieClassificationRepository.save(
                MovieClassification.builder().id(1L).name("PG").build()
        );
        Movie movie = Movie.builder().title(title.toLowerCase()).description("test").classification(classification).build();

        movieRepository.save(movie);

        // when
        // then
        assertThat(movieRepository.existsByTitleIgnoreCase(title)).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists: Not Found")
    void testCanCheckNameExistsNotFound() {
        // given
        String title = "Saw";

        // when
        // then
        assertThat(movieRepository.existsByTitleIgnoreCase(title)).isFalse();
    }
}