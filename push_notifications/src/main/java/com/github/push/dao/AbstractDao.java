package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDao<T> extends CacheLoader<String, List<T>> implements Dao<T> {
    private final Class<T> tClass;
    private final String collectionName;
    private Firestore db;
    Logger logger = LoggerFactory.getLogger(AbstractDao.class);
    ObjectMapper objectMapper = new ObjectMapper();
    LoadingCache<String, List<T>> cache;

    public AbstractDao(Firestore firestore, Class<T> tClass, String collectionName,
                       int cacheExpireAfterAccess, int cacheMaximumSize) {
        this.db = firestore;
        this.tClass = tClass;
        this.collectionName = collectionName;
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(cacheExpireAfterAccess, TimeUnit.HOURS)
                .maximumSize(cacheMaximumSize).build(this);
    }

    T readObject(Map<String, Object> data) throws JsonProcessingException {
        return objectMapper.readValue(objectMapper.writeValueAsString(data), tClass
        );
    }

    CollectionReference getCollection() {
        return db.collection(collectionName);
    }

    public String getCollectionName() {
        return collectionName;
    }
}
