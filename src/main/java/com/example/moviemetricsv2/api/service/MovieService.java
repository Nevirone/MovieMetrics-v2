package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.repository.IMovieClassificationRepository;
import com.example.moviemetricsv2.api.repository.IMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService implements IObjectService<Movie, MovieDto> {
    private final IMovieRepository movieRepository;
    private final IMovieClassificationRepository movieClassificationRepository;

    @Override
    public Movie create(MovieDto movieDto) throws DataConflictException, NotFoundException {
        if (movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
            throw DataConflictException.titleTaken(movieDto.getTitle());

        // todo move to existById and getReferenceById
        MovieClassification movieClassification = movieClassificationRepository.findById(movieDto.getClassificationId())
                .orElseThrow(() -> NotFoundException.movieClassificationNotFoundById(movieDto.getClassificationId()));

        return movieRepository.save(
                Movie.builder()
                        .title(movieDto.getTitle())
                        .description(movieDto.getDescription())
                        .classification(movieClassification)
                        .build()
        );
    }

    @Override
    public Movie get(Long id) throws NotFoundException {
        return movieRepository.findById(id)
                .orElseThrow(() -> NotFoundException.movieNotFoundById(id));
    }

    public Movie getByTitle(String title) throws NotFoundException {
        return movieRepository.findByTitleIgnoreCase(title)
                .orElseThrow(() -> NotFoundException.movieNotFoundByTitle(title));
    }

    @Override
    public List<Movie> getAll() {
        return movieRepository.findAll();
    }

    @Override
    public Movie update(Long id, MovieDto movieDto) throws DataConflictException, NotFoundException {
        if (!movieRepository.existsById(id))
            throw NotFoundException.movieNotFoundById(id);

        if (movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
            throw DataConflictException.titleTaken(movieDto.getTitle());

        // todo move to existById and getReferenceById
        MovieClassification movieClassification = movieClassificationRepository.findById(movieDto.getClassificationId())
                .orElseThrow(() -> NotFoundException.movieClassificationNotFoundById(movieDto.getClassificationId()));

        return movieRepository.save(
                Movie.builder()
                        .id(id)
                        .title(movieDto.getTitle())
                        .description(movieDto.getDescription())
                        .classification(movieClassification)
                        .build()
        );
    }

    @Override
    public Movie delete(Long id) throws NotFoundException {
        Optional<Movie> found = movieRepository.findById(id);

        if (found.isEmpty())
            throw NotFoundException.movieNotFoundById(id);

        movieRepository.deleteById(id);

        return found.get();
    }
}
