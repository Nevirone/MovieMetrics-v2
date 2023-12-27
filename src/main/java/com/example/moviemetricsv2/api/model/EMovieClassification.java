package com.example.moviemetricsv2.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EMovieClassification {
    GeneralAudience("General Audience", "G"),
    ParentalGuideline("Parental Guidance Suggested", "PG"),
    ParentsStronglyCautioned("Parents Strongly Cautioned", "PG-13"),
    Restricted("Restricted", "R"),
    ClearlyAdult("Clearly Adult", "NC-17");

    private final String name;
    private final String brief;
}
