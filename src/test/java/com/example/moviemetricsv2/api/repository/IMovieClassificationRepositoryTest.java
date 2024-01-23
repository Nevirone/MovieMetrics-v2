package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.MovieClassification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IMovieClassificationRepositoryTest {
    @Autowired
    IMovieClassificationRepository movieClassificationRepository;

    @Test
    @DisplayName("Find Classification By Name: Successful")
    void testCanFindClassificationByName() {
        // given
        String name = "PG";
        MovieClassification movieClassification = MovieClassification.builder().id(1L).name(name).build();

        movieClassificationRepository.save(movieClassification);

        // when
        Optional<MovieClassification> found = movieClassificationRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(movieClassification.getName());
    }

    @Test
    @DisplayName("Find Classification By Name Case Insensitive: Successful")
    void testCanFindClassificationByNameCaseInsensitive() {
        // given
        String name = "Pg";
        MovieClassification movieClassification = MovieClassification.builder().id(1L).name(name.toLowerCase()).build();

        movieClassificationRepository.save(movieClassification);

        // when
        Optional<MovieClassification> found = movieClassificationRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo(movieClassification.getName());
    }

    @Test
    @DisplayName("Find Classification By Name: Not Found")
    void testCanFindByNameIfClassificationDoesNotExist() {
        // given
        String name = "PG";

        // when
        Optional<MovieClassification> found = movieClassificationRepository.findByNameIgnoreCase(name);

        // then
        assertThat(found.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Check Name Exists: Successful")
    void testCanCheckNameExists() {
        // given
        String name = "Pg";
        MovieClassification movieClassification = MovieClassification.builder().id(1L).name(name).build();

        movieClassificationRepository.save(movieClassification);

        // when
        // then
        assertThat(movieClassificationRepository.existsByNameIgnoreCase(name)).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists Case Insensitive: Successful")
    void testCanCheckNameExistsCaseInsensitive() {
        // given
        String name = "PG";
        MovieClassification movieClassification = MovieClassification.builder().id(1L).id(1L).name(name.toLowerCase()).build();

        movieClassificationRepository.save(movieClassification);

        // when
        // then
        assertThat(movieClassificationRepository.existsByNameIgnoreCase(name)).isTrue();
    }

    @Test
    @DisplayName("Check Name Exists: Not Found")
    void testCanCheckNameExistsNotFound() {
        // given
        String name = "PG";

        // when
        // then
        assertThat(movieClassificationRepository.existsByNameIgnoreCase(name)).isFalse();
    }
}