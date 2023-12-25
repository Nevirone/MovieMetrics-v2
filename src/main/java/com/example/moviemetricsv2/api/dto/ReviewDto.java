package com.example.moviemetricsv2.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReviewDto {
    @NotNull(message = "Movie Id must be provided")
    private Long movieId;

    @NotNull(message = "Score cannot be empty")
    @Min(value = 1, message = "Score cannot be lower than 1")
    @Max(value = 5, message = "Score cannot be higher than 5")
    private Short score;

    @Size(max = 2048, message = "Content cannot be longer then 2048 characters")
    private String content;
}
