package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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

    List<T> getObjects(String w, String v) throws ExecutionException, InterruptedException, JsonProcessingException {
        Query query = getCollection().whereEqualTo(w, v);
        ApiFuture<QuerySnapshot> snap = query.get();
        List<T> list = new ArrayList<>();
        for (QueryDocumentSnapshot queryDocumentSnapshot : snap.get().getDocuments()
        ) {
            list.add(readObject(queryDocumentSnapshot.getData()));
        }
        return list;
    }

    @Override
    public T getObject(String id) {
        List<T> list = null;
        try {
            list = cache.get("uuid:" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
