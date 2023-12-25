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
import lombok.RequiredArgsConstructor;
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

        if (reviewRepository.existsByMovieIdAndAuthorId(reviewDto.getMovieId(), user.getId()))
            throw new DataConflictException("Review already exists"); // todo move to DataConflictException func

        if (!movieRepository.existsById(reviewDto.getMovieId()))
            throw NotFoundException.movieNotFoundById(reviewDto.getMovieId());

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
                .orElseThrow(() -> new NotFoundException("Review with id " + id + " not found")); // todo move to NotFoundException func
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    public List<Review> getAllOfMovie(Long movieId) {
        return reviewRepository.findAllByMovieId(movieId);
    }
    public List<Review> getAllOfUser(Long userId) {
        return reviewRepository.findAllByAuthorId(userId);
    }

    public Review updateOwn(Long id, ReviewDto reviewDto) throws DataConflictException, NotFoundException, PermissionException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Review> found = reviewRepository.findById(id);

        if (found.isEmpty())
            throw new NotFoundException("Review with id " + id + " not found"); // todo move to NotFoundException func

        if (!Objects.equals(found.get().getAuthor().getId(), user.getId()))
            throw new PermissionException("You are not the owner");

        return reviewRepository.save(
                Review.builder()
                        .movie(movieRepository.getReferenceById(found.get().getMovie().getId()))
                        .author(user)
                        .score(reviewDto.getScore())
                        .content(reviewDto.getContent())
                        .build()
        );
    }

    public Review update(Long id, ReviewDto reviewDto) throws DataConflictException, NotFoundException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Review> found = reviewRepository.findById(id);

        if (found.isEmpty())
            throw new NotFoundException("Review with id " + id + " not found"); // todo move to NotFoundException func

        return reviewRepository.save(
                Review.builder()
                        .movie(movieRepository.getReferenceById(found.get().getMovie().getId()))
                        .author(user)
                        .score(reviewDto.getScore())
                        .content(reviewDto.getContent())
                        .build()
        );
    }

    public Review deleteOwn(Long id) throws NotFoundException, PermissionException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Review> found = reviewRepository.findById(id);

        if (found.isEmpty())
            throw new NotFoundException("Review with id " + id + " not found"); // todo move to NotFoundException func

        if (!Objects.equals(found.get().getAuthor().getId(), user.getId()))
            throw new PermissionException("You are not the owner");

        reviewRepository.deleteById(id);

        return found.get();
    }

    public Review delete(Long id) throws NotFoundException {
        Optional<Review> found = reviewRepository.findById(id);

        if (found.isEmpty())
            throw new NotFoundException("Review with id " + id + " not found"); // todo move to NotFoundException func

        reviewRepository.deleteById(id);

        return found.get();
    }
}
