package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController extends BaseController implements ICrudController<Movie, MovieDto> {
    private final MovieService movieService;
    @Override
    @PreAuthorize("hasAuthority('CREATE_MOVIES')")
    @PostMapping
    public ResponseEntity<Movie> create(@Valid @RequestBody MovieDto movieDto) throws DataConflictException {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.create(movieDto));
    }
    @Override
    @PreAuthorize("hasAuthority('DISPLAY_MOVIES')")
    @GetMapping("/{id}")
    public ResponseEntity<Movie> get(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.get(id));
    }

    @Override
    @PreAuthorize("hasAuthority('DISPLAY_MOVIES')")
    @GetMapping
    public ResponseEntity<List<Movie>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getAll());
    }

    @Override
    @PreAuthorize("hasAuthority('UPDATE_MOVIES')")
    @PatchMapping("/{id}")
    public ResponseEntity<Movie> update(@PathVariable Long id, @Valid @RequestBody MovieDto movieDto) throws DataConflictException, NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.update(id, movieDto));
    }

    @Override
    @PreAuthorize("hasAuthority('DELETE_MOVIES')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Movie> delete(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.delete(id));
    }
}