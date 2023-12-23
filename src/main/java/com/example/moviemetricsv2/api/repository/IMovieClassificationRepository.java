package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.MovieClassification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMovieClassificationRepository extends JpaRepository<MovieClassification, Long> {
    Optional<MovieClassification> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}