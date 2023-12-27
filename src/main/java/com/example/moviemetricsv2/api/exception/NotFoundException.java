package com.example.moviemetricsv2.api.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException userNotFoundById(Long id) {
        return new NotFoundException("User with id " + id + " not found");
    }

    public static NotFoundException userNotFoundByEmail(String email) {
        return new NotFoundException("User with email " + email + " not found");
    }

    public static NotFoundException roleNotFoundById(Long id) {
        return new NotFoundException("Role with id " + id + " not found");
    }

    public static NotFoundException roleNotFoundByName(String name) {
        return new NotFoundException("Role with name " + name + " not found");
    }

    public static NotFoundException movieClassificationNotFoundById(Long id) {
        return new NotFoundException("Movie classification with id " + id + " not found");
    }

    public static NotFoundException movieClassificationNotFoundByName(String name) {
        return new NotFoundException("Movie classification with name " + name + " not found");
    }

    public static NotFoundException movieNotFoundById(Long id) {
        return new NotFoundException("Movie with id " + id + " not found");
    }

    public static NotFoundException movieNotFoundByTitle(String title) {
        return new NotFoundException("Movie with title " + title + " not found");
    }

    public static NotFoundException reviewNotFoundById(Long id) {
        return new NotFoundException("Review with id " + id + " not found");
    }

    public static NotFoundException genreNotFoundById(Long id) {
        return new NotFoundException("Genre with id " + id + " not found");
    }
}
