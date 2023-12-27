package com.example.moviemetricsv2.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EGenre {
    Action("Action"),
    Adventure("Adventure"),
    Animation("Animation"),
    Biography("Biography"),
    Comedy("Comedy"),
    Crime("Crime"),
    Documentary("Documentary"),
    Drama("Drama"),
    Family("Family"),
    Fantasy("Fantasy"),
    History("History"),
    Horror("Horror"),
    Music("Music"),
    Musical("Musical"),
    Mystery("Mystery"),
    Other("Other"),
    Romance("Romance"),
    SciFi("Science Fiction"),
    Sport("Sport"),
    Thriller("Thriller"),
    Western("Western");

    private final String name;
}
