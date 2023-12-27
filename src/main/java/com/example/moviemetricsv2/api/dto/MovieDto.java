package com.example.moviemetricsv2.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MovieDto {
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, message = "Title must be at least 3 characters long")
    @Size(max = 64, message = "Title can be at most 64 characters long")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    @Size(max = 64, message = "Title can be at most 2048 characters long")
    private String description;

    @NotNull(message = "Genre Ids must be provided")
    private List<Long> genreIds;

    @NotNull(message = "Classification Id must be provided")
    @Min(value = 1, message = "Classification Id must be valid")
    private Long classificationId;
}
