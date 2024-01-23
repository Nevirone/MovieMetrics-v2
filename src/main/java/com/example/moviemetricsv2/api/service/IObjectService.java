package com.example.moviemetricsv2.api.service;

import com.example.moviemetricsv2.api.exception.DataConflictException;
import com.example.moviemetricsv2.api.exception.NotFoundException;

import java.util.List;

public interface IObjectService<T, TDto> {
    T create(TDto dto) throws DataConflictException;

    T get(Long id) throws NotFoundException;

    List<T> getAll();

    T update(Long id, TDto dto) throws DataConflictException, NotFoundException;

    T delete(Long id) throws NotFoundException;
}
