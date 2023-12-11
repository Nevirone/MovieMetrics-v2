package com.example.moviemetricsv2.api.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EPermission {
    DISPLAY_MOVIES,
    CREATE_MOVIES,
    UPDATE_MOVIES,
    DELETE_MOVIES,
    CREATE_COMMENTS,
    DISPLAY_ALL_COMMENTS,
    UPDATE_OWN_COMMENTS,
    UPDATE_ANY_COMMENT,
    DELETE_OWN_COMMENTS,
    DELETE_ANY_COMMENT,
    DISPLAY_USERS,
    CREATE_USERS,
    UPDATE_USERS,
    DELETE_USERS;
}
