package com.example.moviemetricsv2.api.response;

import com.example.moviemetricsv2.api.model.Genre;
import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.MovieClassification;
import com.example.moviemetricsv2.api.model.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private String genres;

    public MovieResponse(Movie movie) {
        id = movie.getId();
        title = movie.getTitle();
        description = movie.getDescription();
        genres = String.join(", ", movie.getGenres().stream().map(Genre::getName).toList());
    }
}
