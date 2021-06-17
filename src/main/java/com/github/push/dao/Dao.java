package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Dao<T> {
    List<T> getObjects(int offset, int limit) throws ExecutionException, InterruptedException, JsonProcessingException;
    T getObject(String id) throws ExecutionException, InterruptedException, JsonProcessingException;
    String setObject(T t) throws ExecutionException, InterruptedException, JsonProcessingException;
    boolean isExist(T t) throws ExecutionException, InterruptedException;
}
