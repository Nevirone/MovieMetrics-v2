package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;

import java.util.List;

public interface IObjectService<T, TDto, TResponse> {
    TResponse create(TDto dto) throws DataConflictException;

    TResponse get(Long id) throws NotFoundException;

    List<TResponse> getAll();

    TResponse update(Long id, TDto dto) throws DataConflictException, NotFoundException;

    TResponse delete(Long id) throws NotFoundException;
}
