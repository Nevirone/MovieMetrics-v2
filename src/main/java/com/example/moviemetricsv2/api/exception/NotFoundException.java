package com.example.moviemetricsv2.api.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException userNotFoundById(Long id) {
        return new NotFoundException("User with id " + id + " not found");
    }

    public static NotFoundException userNotFoundByEmail(String email) {
        return new NotFoundException("User with email " + email + " not found");
    }

    public static NotFoundException roleNotFound(String name) {
        return new NotFoundException("Role with name " + name + " not found");
    }
}
