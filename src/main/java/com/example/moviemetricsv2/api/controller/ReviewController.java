package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.dto.ReviewDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.exception.PermissionException;
import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.Review;
import com.example.moviemetricsv2.api.model.User;
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
public class ReviewController extends BaseController implements ICrudController<Review, ReviewDto> {
    private final ReviewService reviewService;

    @Override
    @PreAuthorize("hasAuthority('CREATE_REVIEWS')")
    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody ReviewDto reviewDto) throws DataConflictException {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(reviewDto));
    }

    @Override
    @PreAuthorize("hasAuthority('DISPLAY_REVIEWS')")
    @GetMapping("/{id}")
    public ResponseEntity<Review> get(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.get(id));
    }

    @Override
    @PreAuthorize("hasAuthority('DISPLAY_REVIEWS')")
    @GetMapping
    public ResponseEntity<List<Review>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAll());
    }

    @PreAuthorize("hasAuthority('DISPLAY_REVIEWS')")
    @GetMapping("/of/{movieId}")
    public ResponseEntity<List<Review>> getAllOfMovie(@PathVariable Long movieId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAllOfMovie(movieId));
    }

    @PreAuthorize("hasAuthority('DISPLAY_REVIEWS')")
    @GetMapping("/from/{userId}")
    public ResponseEntity<List<Review>> getAllFromUser(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAllOfUser(userId));
    }

    @PreAuthorize("hasAuthority('DISPLAY_REVIEWS')")
    @GetMapping("/own")
    public ResponseEntity<List<Review>> getAllOfMe() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAllOfMovie(user.getId()));
    }

    @PreAuthorize("hasAuthority('UPDATE_OWN_REVIEWS')")
    @PatchMapping("/own/{id}")
    public ResponseEntity<Review> updateOwn(@PathVariable Long id, @Valid @RequestBody ReviewDto reviewDto)
            throws DataConflictException, NotFoundException, PermissionException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.updateOwn(id, reviewDto));
    }

    @PreAuthorize("hasAuthority('DELETE_OWN_REVIEWS')")
    @DeleteMapping("/own/{id}")
    public ResponseEntity<Review> deleteOwn(@PathVariable Long id) throws NotFoundException, PermissionException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.deleteOwn(id));
    }

    @Override
    @PreAuthorize("hasAuthority('UPDATE_REVIEWS')")
    @PatchMapping("/{id}")
    public ResponseEntity<Review> update(@PathVariable Long id, @Valid @RequestBody ReviewDto reviewDto) throws DataConflictException, NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.update(id, reviewDto));
    }

    @Override
    @PreAuthorize("hasAuthority('DELETE_REVIEWS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Review> delete(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.delete(id));
    }
}
