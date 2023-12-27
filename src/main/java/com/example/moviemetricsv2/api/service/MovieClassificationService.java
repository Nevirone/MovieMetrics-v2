package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.model.Permission;
import com.example.moviemetricsv2.api.repository.IMovieClassificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieClassificationService {
    private final IMovieClassificationRepository movieClassificationRepository;

    public List<MovieClassification> getAll() {
        return movieClassificationRepository.findAll();
    }
    public MovieClassification findOrCreate(Long id, String name, String brief) {
        return movieClassificationRepository.findByNameIgnoreCase(name).orElseGet(() -> movieClassificationRepository.save(
                MovieClassification.builder()
                        .id(id)
                        .name(name)
                        .brief(brief)
                        .build()
        ));
    }

    public void createIfNotFound(Long id, String name, String brief) {
        if (!movieClassificationRepository.existsByNameIgnoreCase(name))
            movieClassificationRepository.save(
                    MovieClassification.builder()
                            .id(id)
                            .name(name)
                            .brief(brief)
                            .build()
            );
    }
}

