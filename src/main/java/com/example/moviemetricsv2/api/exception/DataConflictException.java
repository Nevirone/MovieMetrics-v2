package com.example.moviemetricsv2.api.exception;

public class DataConflictException extends RuntimeException{

    public DataConflictException(String message) {
        super(message);
    }

    public static DataConflictException emailTaken(String email) {
        return new DataConflictException("Email " + email + " is taken");
    }

    public static DataConflictException nameTaken(String name) {
        return new DataConflictException("Name " + name + " is taken");
    }

    public static DataConflictException titleTaken(String title) {
        return new DataConflictException("Title " + title + " is taken");
    }
}
