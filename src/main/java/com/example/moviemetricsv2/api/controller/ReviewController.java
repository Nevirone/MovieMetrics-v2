package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.dto.ReviewDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.exception.PermissionException;
import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.Review;
import com.example.moviemetricsv2.api.model.User;
import com.example.moviemetricsv2.api.response.ReviewResponse;
import com.example.moviemetricsv2.api.service.MovieService;
import com.example.moviemetricsv2.api.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController extends BaseController {
    private final ReviewService reviewService;

    @PreAuthorize("hasAuthority('CREATE_REVIEWS')")
    @PostMapping("/of/{movieId}")
    public ResponseEntity<ReviewResponse> create(@PathVariable Long movieId, @Valid @RequestBody ReviewDto reviewDto)
            throws DataConflictException {
        reviewDto.setMovieId(movieId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(reviewDto));
    }

    @PreAuthorize("hasAuthority('DISPLAY_REVIEWS')")
    @GetMapping("/own")
    public ResponseEntity<List<ReviewResponse>> getAllFromMe() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAllOfUser(user.getId()));
    }

    @PreAuthorize("hasAuthority('DISPLAY_REVIEWS')")
    @GetMapping("/from/{userId}")
    public ResponseEntity<List<ReviewResponse>> getAllFromUser(@PathVariable Long userId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAllOfUser(userId));
    }

    @PreAuthorize("hasAuthority('DISPLAY_REVIEWS')")
    @GetMapping("/of/{movieId}")
    public ResponseEntity<List<ReviewResponse>> getAllOfMovie(@PathVariable Long movieId) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAllOfMovie(movieId));
    }

    @PreAuthorize("hasAuthority('UPDATE_OWN_REVIEWS')")
    @PatchMapping("/own/{id}")
    public ResponseEntity<ReviewResponse> updateOwn(@PathVariable Long id, @Valid @RequestBody ReviewDto reviewDto)
            throws NotFoundException, PermissionException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.updateOwn(id, reviewDto));
    }

    @PreAuthorize("hasAuthority('UPDATE_REVIEWS')")
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(@PathVariable Long id, @Valid @RequestBody ReviewDto reviewDto)
            throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.update(id, reviewDto));
    }

    @PreAuthorize("hasAuthority('DELETE_OWN_REVIEWS')")
    @DeleteMapping("/own/{id}")
    public ResponseEntity<ReviewResponse> deleteOwn(@PathVariable Long id) throws NotFoundException, PermissionException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.deleteOwn(id));
    }

    @PreAuthorize("hasAuthority('DELETE_REVIEWS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewResponse> delete(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.delete(id));
    }
}
