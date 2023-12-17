package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IMovieClassificationRepository extends JpaRepository<MovieClassification, Long> {
    Optional<MovieClassification> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}