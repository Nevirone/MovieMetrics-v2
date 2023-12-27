package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICrudController<T, TDto, TResponse> {
    ResponseEntity<TResponse> create(TDto dto) throws DataConflictException;

    ResponseEntity<TResponse> get(Long id) throws NotFoundException;

    ResponseEntity<List<TResponse>> getAll();

    ResponseEntity<TResponse> update(Long id, TDto dto) throws DataConflictException, NotFoundException;

    ResponseEntity<TResponse> delete(Long id) throws NotFoundException;
}
