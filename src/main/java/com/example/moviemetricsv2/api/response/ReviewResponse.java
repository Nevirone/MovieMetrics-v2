package com.example.moviemetricsv2.api.response;

import com.example.moviemetricsv2.api.model.Movie;
import com.example.moviemetricsv2.api.model.Review;
import com.example.moviemetricsv2.api.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Short score;
    private String content;

    public ReviewResponse(Review review) {
        id = review.getId();
        score = review.getScore();
        content = review.getContent();
    }
}
