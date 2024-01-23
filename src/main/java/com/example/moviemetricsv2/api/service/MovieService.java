package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.model.Genre;
import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.repository.IGenreRepository;
import com.example.moviemetricsv2.api.repository.IMovieClassificationRepository;
import com.example.moviemetricsv2.api.repository.IMovieRepository;
import com.example.moviemetricsv2.api.response.MovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService implements IObjectService<Movie, MovieDto> {
    private final IMovieRepository movieRepository;
    private final IMovieClassificationRepository movieClassificationRepository;
    private final IGenreRepository genreRepository;

    @Override
    public Movie create(MovieDto movieDto) throws DataConflictException, NotFoundException {
        if (movieRepository.existsByTitleIgnoreCase(movieDto.getTitle()))
            throw DataConflictException.titleTaken(movieDto.getTitle());

        if (!movieClassificationRepository.existsById(movieDto.getClassificationId()))
            throw NotFoundException.movieClassificationNotFoundById(movieDto.getClassificationId());

        List<Genre> genres = movieDto.getGenreIds().stream().map(genreId -> {
            if (!genreRepository.existsById(genreId))
                throw NotFoundException.genreNotFoundById(genreId);
            else
                return genreRepository.getReferenceById(genreId);
        }).toList();

        return movieRepository.save(
                Movie.builder()
                        .title(movieDto.getTitle())
                        .description(movieDto.getDescription())
                        .genres(genres)
                        .classification(movieClassificationRepository.getReferenceById(movieDto.getClassificationId()))
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

        Optional<Movie> found = movieRepository.findByTitleIgnoreCase(movieDto.getTitle());

        if (found.isPresent() && !found.get().getId().equals(id))
            throw DataConflictException.titleTaken(movieDto.getTitle());

        if (!movieClassificationRepository.existsById(movieDto.getClassificationId()))
            throw NotFoundException.movieClassificationNotFoundById(movieDto.getClassificationId());

        List<Genre> genres = movieDto.getGenreIds().stream().map(genreId -> {
            if (!genreRepository.existsById(genreId))
                throw NotFoundException.genreNotFoundById(genreId);
            else
                return genreRepository.getReferenceById(genreId);
        }).toList();

        return movieRepository.save(
                Movie.builder()
                        .id(id)
                        .title(movieDto.getTitle())
                        .description(movieDto.getDescription())
                        .genres(genres)
                        .classification(movieClassificationRepository.getReferenceById(movieDto.getClassificationId()))
                        .build()
        );
    }

    @Override
    public Movie delete(Long id) throws NotFoundException {
        Movie found = movieRepository.findById(id)
                .orElseThrow(() -> NotFoundException.movieNotFoundById(id));

        movieRepository.deleteById(id);

        return found;
    }
}
