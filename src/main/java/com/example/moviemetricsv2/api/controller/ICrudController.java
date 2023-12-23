package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICrudController<T, TDto> {
    ResponseEntity<T> create(TDto dto) throws DataConflictException;

    ResponseEntity<T> get(Long id) throws NotFoundException;

    ResponseEntity<List<T>> getAll();

    ResponseEntity<T> update(Long id, TDto dto) throws DataConflictException, NotFoundException;

    ResponseEntity<T> delete(Long id) throws NotFoundException;
}
