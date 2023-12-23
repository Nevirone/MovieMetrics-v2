package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.repository.IMovieClassificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class MovieClassificationServiceTest {
    private AutoCloseable autoCloseable;
    @Mock
    private IMovieClassificationRepository movieClassificationRepository;

    private MovieClassificationService movieClassificationService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        movieClassificationService = new MovieClassificationService(movieClassificationRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Find Or Create: Found")
    void canFindOrCreateWhenFound() {
        // given
        String name = "PG";
        given(movieClassificationRepository.findByNameIgnoreCase(name))
                .willReturn(Optional.of(MovieClassification.builder().name(name).build()));

        // when
        movieClassificationService.findOrCreate(name);

        // then
        verify(movieClassificationRepository).findByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Find Or Create: Created")
    void canFindOrCreateWhenNotFound() {
        // given
        String name = "PG";
        given(movieClassificationRepository.findByNameIgnoreCase(name))
                .willReturn(Optional.empty());

        // when
        movieClassificationService.findOrCreate(name);

        // then
        ArgumentCaptor<MovieClassification> movieClassificationArgumentCaptor = ArgumentCaptor.forClass(MovieClassification.class);

        verify(movieClassificationRepository).findByNameIgnoreCase(name);
        verify(movieClassificationRepository).save(movieClassificationArgumentCaptor.capture());

        MovieClassification capturedMovieClassification = movieClassificationArgumentCaptor.getValue();

        assertThat(capturedMovieClassification.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create If Not Found: Found")
    void canCreateIfNotFoundWhenFound() {
        // given
        String name = "PG";
        given(movieClassificationRepository.existsByNameIgnoreCase(name))
                .willReturn(true);

        // when
        movieClassificationService.createIfNotFound(name);

        // then
        verify(movieClassificationRepository).existsByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Create If Not Found: Not Found")
    void canCreateIfNotFoundWhenNotFound() {
        // given
        String name = "PG";
        given(movieClassificationRepository.existsByNameIgnoreCase(name))
                .willReturn(false);

        // when
        movieClassificationService.createIfNotFound(name);

        // then
        ArgumentCaptor<MovieClassification> movieClassificationArgumentCaptor = ArgumentCaptor.forClass(MovieClassification.class);

        verify(movieClassificationRepository).existsByNameIgnoreCase(name);
        verify(movieClassificationRepository).save(movieClassificationArgumentCaptor.capture());

        MovieClassification capturedMovieClassification = movieClassificationArgumentCaptor.getValue();

        assertThat(capturedMovieClassification.getName()).isEqualTo(name);
    }
}