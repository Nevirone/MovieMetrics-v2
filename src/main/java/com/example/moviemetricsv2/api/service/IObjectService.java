package com.example.moviemetricsv2.api.service;

import org.springframework.stereotype.Service;

import java.util.List;

public interface IObjectService<T, TDto> {
    T create(TDto dto);

    T get(Long id);
    List<T> getAll();
    T update(Long id, TDto dto);
    T delete(Long id);
}
