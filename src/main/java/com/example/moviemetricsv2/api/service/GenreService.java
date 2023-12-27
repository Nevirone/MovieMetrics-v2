package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.model.Genre;
import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.model.Permission;
import com.example.moviemetricsv2.api.model.Role;
import com.example.moviemetricsv2.api.repository.IGenreRepository;
import com.example.moviemetricsv2.api.repository.IPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final IGenreRepository genreRepository;

    public List<Genre> getAll() {
        return genreRepository.findAll();
    }

    public Genre findOrCreate(Long id, String name) {
        return genreRepository.findByNameIgnoreCase(name).orElseGet(() -> genreRepository.save(
                Genre.builder()
                        .id(id)
                        .name(name)
                        .build()
        ));
    }

    public void createIfNotFound(Long id, String name) {
        if (!genreRepository.existsByNameIgnoreCase(name))
            genreRepository.save(
                    Genre.builder()
                            .id(id)
                            .name(name)
                            .build()
            );
    }
}
