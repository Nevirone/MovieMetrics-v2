package com.example.moviemetricsv2.api.repository;

import com.example.moviemetricsv2.api.model.Permission;
import com.example.moviemetricsv2.api.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByMovieIdAndAuthorId(Long movieId, Long authorId);

    List<Review> findAllByMovieId(Long movieId);
    List<Review> findAllByAuthorId(Long authorId);
}
