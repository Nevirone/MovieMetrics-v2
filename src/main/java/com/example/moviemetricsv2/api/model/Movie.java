package com.example.moviemetricsv2.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(length = 64)
    private String title;

    @Column(length = 2048)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_classification_id", nullable = false)
    private MovieClassification classification;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "genre_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private List<Genre> genres;

    @OneToMany(mappedBy = "movie")
    private List<Review> reviews;
}
