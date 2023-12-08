package com.example.moviemetricsv2.api.service;

import org.springframework.stereotype.Service;

import java.util.List;

public interface IObjectService<T, TDto> {
    public T create(TDto dto);

    public T get(Long id);
    public List<T> getAll();
    public T update(Long id, TDto dto);
    public T delete(Long id);
}
