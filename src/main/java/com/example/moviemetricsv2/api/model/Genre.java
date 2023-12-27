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
@Table(name = "genres")
public class Genre {
    @Id
    private Long id;

    @Column(length = 32)
    private String name;

    @JsonBackReference
    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies;
}
