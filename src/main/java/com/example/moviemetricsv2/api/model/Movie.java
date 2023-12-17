package com.example.moviemetricsv2.api.model;

import com.example.moviemetricsv2.api.dto.MovieDto;
import com.example.moviemetricsv2.api.dto.UserDto;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import com.example.moviemetricsv2.api.repository.IMovieClassificationRepository;
import com.example.moviemetricsv2.api.repository.IRoleRepository;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinColumn(name = "movie_classification_id", nullable = false)
    private MovieClassification classification;

    public Movie(MovieDto dto, IMovieClassificationRepository movieClassificationRepository) throws NotFoundException {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.classification = movieClassificationRepository.findById(dto.getClassification())
                .orElseThrow(() -> NotFoundException.movieClassificationNotFoundById(dto.getClassification()));
    }
}
