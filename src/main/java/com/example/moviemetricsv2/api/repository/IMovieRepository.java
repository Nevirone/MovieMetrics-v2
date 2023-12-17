package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitleIgnoreCase(String title);
    boolean existsByTitleIgnoreCase(String title);
}