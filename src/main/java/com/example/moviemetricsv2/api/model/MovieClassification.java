package com.example.moviemetricsv2.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "movie_classifications")
public class MovieClassification {
    @Id
    private Long id;

    @Column(length = 32)
    private String name;

    @Column(length = 5)
    private String brief;

    @JsonBackReference
    @OneToMany(mappedBy = "classification")
    private Set<Movie> movies;
}
