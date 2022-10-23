package com.example.imdbplus.repository;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseRepositoryInterface<T> {

    public ResponseEntity save(T entity);
    public T getEntity(String entityId);
    public List<T> getAllEntity();
    public String delete(String entityId, String accessToken);
    public String update(String entityId, T entity);
}
