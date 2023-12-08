package com.example.moviemetricsv2.api.controller;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICrudController<T, TDto> {
    public ResponseEntity<T> create(TDto dto) throws DataConflictException;

    public ResponseEntity<T> get(Long id) throws NotFoundException;

    public ResponseEntity<List<T>> getAll();
    public ResponseEntity<T> update(Long id, TDto dto) throws  DataConflictException, NotFoundException;
    public ResponseEntity<T> delete(Long id) throws NotFoundException;
}
