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
public class ReviewService implements IObjectService<Review, ReviewDto, ReviewResponse>{
    private final IReviewRepository reviewRepository;
    private final IMovieRepository movieRepository;
    private final IUserRepository userRepository;

    public ReviewResponse create(ReviewDto reviewDto) throws DataConflictException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!movieRepository.existsById(reviewDto.getMovieId()))
            throw NotFoundException.movieNotFoundById(reviewDto.getMovieId());

        if (reviewRepository.existsByMovieIdAndAuthorId(reviewDto.getMovieId(), user.getId()))
            throw DataConflictException.reviewExists(user.getId(), reviewDto.getMovieId());


        Review created = reviewRepository.save(Review.builder()
                .movie(movieRepository.getReferenceById(reviewDto.getMovieId()))
                .author(user)
                .score(reviewDto.getScore())
                .content(reviewDto.getContent())
                .build()
        );
        return new ReviewResponse(created);
    }

    public ReviewResponse get(Long id) throws NotFoundException {
        Review found = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));
        return new ReviewResponse(found);
    }

    public List<ReviewResponse> getAll() {
        return reviewRepository.findAll().stream().map(ReviewResponse::new).toList();
    }

    public List<ReviewResponse> getAllOfMovie(Long movieId) throws NotFoundException {
        if (!movieRepository.existsById(movieId))
            throw NotFoundException.movieNotFoundById(movieId);

        return reviewRepository.findAllByMovieId(movieId).stream().map(ReviewResponse::new).toList();
    }
    public List<ReviewResponse> getAllOfUser(Long userId) throws NotFoundException {
        if (!userRepository.existsById(userId))
            throw NotFoundException.userNotFoundById(userId);

        return reviewRepository.findAllByAuthorId(userId).stream().map(ReviewResponse::new).toList();
    }

    public ReviewResponse updateOwn(Long id, ReviewDto reviewDto) throws NotFoundException, PermissionException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));

        if (!review.getAuthor().getId().equals(user.getId()))
            throw new PermissionException("You are not the author");

        review.setScore(reviewDto.getScore());
        review.setContent(reviewDto.getContent());

        Review saved = reviewRepository.save(review);
        return new ReviewResponse(saved);
    }

    public ReviewResponse update(Long id, ReviewDto reviewDto) throws NotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));

        review.setScore(reviewDto.getScore());
        review.setContent(reviewDto.getContent());

        Review updated = reviewRepository.save(review);
        return new ReviewResponse(updated);
    }

    public ReviewResponse deleteOwn(Long id) throws NotFoundException, PermissionException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));

        if (!review.getAuthor().getId().equals(user.getId()))
            throw new PermissionException("You are not the author");

        reviewRepository.deleteById(id);

        return new ReviewResponse(review);
    }

    public ReviewResponse delete(Long id) throws NotFoundException {
        Review found = reviewRepository.findById(id)
                .orElseThrow(() -> NotFoundException.reviewNotFoundById(id));

        reviewRepository.deleteById(id);

        return new ReviewResponse(found);
    }
}
