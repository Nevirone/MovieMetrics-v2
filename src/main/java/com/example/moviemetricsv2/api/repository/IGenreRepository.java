package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IGenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
