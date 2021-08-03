package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractDao<T> implements Dao<T> {
    private final Class<T> tClass;
    private final String collectionName;
    private Firestore db;
    Logger logger = LoggerFactory.getLogger(AbstractDao.class);
    ObjectMapper objectMapper = new ObjectMapper();

    public AbstractDao(Firestore firestore, Class<T> tClass, String collectionName) {
        this.db = firestore;
        this.tClass = tClass;
        this.collectionName = collectionName;
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
