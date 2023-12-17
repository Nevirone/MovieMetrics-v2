package com.example.moviemetricsv2.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MovieDto {
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, message = "Title must be at least 3 characters long")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String description;

    @NotNull(message = "Classification must be provided")
    private Long classification;
}
