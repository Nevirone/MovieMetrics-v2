package com.example.moviemetricsv2.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EMovieClassification {
    GeneralAudience("G"),
    ParentalGuideline("PG"),
    ParentsStronglyCautioned("PG-13"),
    Restricted("R"),
    ClearlyAdult("NC-17");

    private final String name;
}
