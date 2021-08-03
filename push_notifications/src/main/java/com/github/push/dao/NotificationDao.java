package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Notification;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NotificationDao extends AbstractDao<Notification> {

    public NotificationDao(Firestore firestore, String path) {
        super(firestore, Notification.class, path + "notifications");
    }

    @Override
    public List<Notification> getObjects(int offset, int limit) throws ExecutionException, InterruptedException, JsonProcessingException {
        ApiFuture<QuerySnapshot> query = getCollection().offset(offset).limit(limit).get();
        List<Notification> list = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : query.get().getDocuments()) {
            list.add(readObject(snapshot.getData()));
        }
        return list;
    }

    @Override
    public Notification getObject(String id) throws ExecutionException, InterruptedException, JsonProcessingException {
        DocumentReference doc = getCollection().document(id);
        logger.info("[getObject]" + doc.getPath());
        if (!doc.get().get().exists()) {
            Notification notification = new Notification();
            notification.setUuid(id);
            id = setObject(notification);
            return getObject(id);
        } else {
            return readObject(doc.get().get().getData());
        }
    }

    @Override
    public String setObject(Notification notification) throws ExecutionException, InterruptedException, JsonProcessingException {
        logger.info("[setObject] notification...");
        boolean exist = isExist(notification);
        Map<String, Object> map = objectMapper.readValue(objectMapper.writeValueAsString(notification), HashMap.class);
        if (exist) {
            getCollection().document(notification.getUuid()).update(map);
        } else {
            logger.info("Creating notification...");
            if (notification.getUuid() != null) {
                ApiFuture<WriteResult> res = getCollection().document(notification.getUuid()).create(map);
            } else {
                ApiFuture<DocumentReference> doc = getCollection().add(map);
                notification.setUuid(doc.get().getId());
            }
        }
        return notification.getUuid();
    }

    @Override
    public boolean isExist(Notification notification) throws ExecutionException, InterruptedException {
        return notification.getUuid() != null && getCollection().document(notification.getUuid()).get().get().exists();
    }

}
