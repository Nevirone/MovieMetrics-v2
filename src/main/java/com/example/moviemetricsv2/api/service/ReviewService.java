package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.dto.ReviewDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.InternalServerException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.exception.PermissionException;
import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.model.Review;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.repository.IMovieRepository;
import com.example.moviemetricsv2.api.repository.IReviewRepository;
import com.example.moviemetricsv2.api.repository.IUserRepository;
import com.example.moviemetricsv2.api.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService implements IObjectService<Review, ReviewDto>{
    private final IReviewRepository reviewRepository;
    private final IMovieRepository movieRepository;
    private final IUserRepository userRepository;

    public Review create(ReviewDto reviewDto) throws DataConflictException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!movieRepository.existsById(reviewDto.getMovieId()))
            throw NotFoundException.movieNotFoundById(reviewDto.getMovieId());

        if (reviewRepository.existsByMovieIdAndAuthorId(reviewDto.getMovieId(), user.getId()))
            throw DataConflictException.reviewExists(user.getId(), reviewDto.getMovieId());


        return reviewRepository.save(Review.builder()
                .movie(movieRepository.getReferenceById(reviewDto.getMovieId()))
                .author(user)
                .score(reviewDto.getScore())
                .content(reviewDto.getContent())
                .build()
        );
    }

    public Review get(Long id) throws NotFoundException {
        return reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    public List<Review> getAllOfMovie(Long movieId) throws NotFoundException {
        if (!movieRepository.existsById(movieId))
            throw NotFoundException.movieNotFoundById(movieId);

        return reviewRepository.findAllByMovieId(movieId);
    }
    public List<Review> getAllOfUser(Long userId) throws NotFoundException {
        if (!userRepository.existsById(userId))
            throw NotFoundException.userNotFoundById(userId);

        return reviewRepository.findAllByAuthorId(userId);
    }

    public Review updateOwn(Long id, ReviewDto reviewDto) throws NotFoundException, PermissionException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));

        if (!review.getAuthor().getId().equals(user.getId()))
            throw new PermissionException("You are not the author");

        review.setScore(reviewDto.getScore());
        review.setContent(reviewDto.getContent());

        return reviewRepository.save(review);
    }

    public Review update(Long id, ReviewDto reviewDto) throws NotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));

        review.setScore(reviewDto.getScore());
        review.setContent(reviewDto.getContent());

        return reviewRepository.save(review);
    }

    public Review deleteOwn(Long id) throws NotFoundException, PermissionException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));

        if (!review.getAuthor().getId().equals(user.getId()))
            throw new PermissionException("You are not the author");

        reviewRepository.deleteById(id);

        return review;
    }

    public Review delete(Long id) throws NotFoundException {
        Review found = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));

        reviewRepository.deleteById(id);

        return found;
    }
}
