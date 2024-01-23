package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.model.Genre;
import com.example.moviemetricsv2.api.model.Permission;
import com.example.moviemetricsv2.api.repository.IGenreRepository;
import com.example.moviemetricsv2.api.repository.IPermissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class GenreServiceTest {
    private AutoCloseable autoCloseable;
    @Mock
    private IGenreRepository genreRepository;

    private GenreService genreService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        genreService = new GenreService(genreRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Find Or Create: Found")
    void canFindOrCreateWhenFound() {
        // given
        String name = "Action";
        given(genreRepository.findByNameIgnoreCase(name))
                .willReturn(Optional.of(Genre.builder().name(name).build()));

        // when
        genreService.findOrCreate(1L, name);

        // then
        verify(genreRepository).findByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Find Or Create: Created")
    void canFindOrCreateWhenNotFound() {
        // given
        String name = "Action";
        given(genreRepository.findByNameIgnoreCase(name))
                .willReturn(Optional.empty());

        // when
        genreService.findOrCreate(1L, name);

        // then
        ArgumentCaptor<Genre> genreArgumentCaptor = ArgumentCaptor.forClass(Genre.class);

        verify(genreRepository).findByNameIgnoreCase(name);
        verify(genreRepository).save(genreArgumentCaptor.capture());

        Genre capturedGenre = genreArgumentCaptor.getValue();

        assertThat(capturedGenre.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create If Not Found: Found")
    void canCreateIfNotFoundWhenFound() {
        // given
        String name = "Action";
        given(genreRepository.existsByNameIgnoreCase(name))
                .willReturn(true);

        // when
        genreService.createIfNotFound(1L, name);

        // then
        verify(genreRepository).existsByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Create If Not Found: Not Found")
    void canCreateIfNotFoundWhenNotFound() {
        // given
        String name = "Action";
        given(genreRepository.existsByNameIgnoreCase(name))
                .willReturn(false);

        // when
        genreService.createIfNotFound(1L, name);

        // then
        ArgumentCaptor<Genre> genreArgumentCaptor = ArgumentCaptor.forClass(Genre.class);

        verify(genreRepository).existsByNameIgnoreCase(name);
        verify(genreRepository).save(genreArgumentCaptor.capture());

        Genre capturedGenre = genreArgumentCaptor.getValue();

        assertThat(capturedGenre.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Get All Permissions: Successful")
    void getAllPermissions() {
        // given
        given(genreRepository.findAll())
                .willReturn(new ArrayList<>());

        // when
        genreService.getAll();

        // then
        verify(genreRepository).findAll();
    }
}