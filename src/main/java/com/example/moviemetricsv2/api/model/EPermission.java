package com.example.moviemetricsv2.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPermission {
    DisplayUsers("DISPLAY_USERS"),
    CreateUsers("CREATE_USERS"),
    UpdateUsers("UPDATE_USERS"),
    DeleteUsers("DELETE_USERS"),
    DisplayMovies("DISPLAY_MOVIES"),
    CreateMovies("CREATE_MOVIES"),
    UpdateMovies("UPDATE_MOVIES"),
    DeleteMovies("DELETE_MOVIES"),
    DisplayReviews("DISPLAY_REVIEWS"),
    CreateReviews("CREATE_REVIEWS"),
    UpdateReviews("UPDATE_REVIEWS"),
    DeleteReviews("DELETE_REVIEWS"),
    UpdateOwnReviews("UPDATE_OWN_REVIEWS"),
    DeleteOwnReviews("DELETE_OWN_REVIEWS");

    private final String name;
}
