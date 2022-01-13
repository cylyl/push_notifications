package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Notification;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class NotificationDao extends AbstractDao<Notification> {

    public NotificationDao(Firestore firestore, String path, int cacheExpireAfterAccess, int cacheMaximumSize) {
        super(firestore, Notification.class, path + "notifications"
                , cacheExpireAfterAccess, cacheMaximumSize);
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
        List<Notification> list = cache.getUnchecked("uuid:" + id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public String setObject(Notification notification) throws ExecutionException, InterruptedException, JsonProcessingException {
        cache.refresh("uuid:" + notification.getUuid());
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
                setObject(notification);
            }
        }
        return notification.getUuid();
    }

    @Override
    public boolean isExist(Notification notification) throws ExecutionException, InterruptedException {
        return notification.getUuid() != null && getCollection().document(notification.getUuid()).get().get().exists();
    }

    @Override
    public List<Notification> load(String id) throws Exception {
        String[] q = id.split(":");
        if (id.startsWith("uuid:")) {
            Notification notification;
            DocumentReference doc = getCollection().document(q[1]);
            logger.info("[getObject]" + doc.getPath());
            if (!doc.get().get().exists()) {
                notification = new Notification();
                notification.setUuid(id);
                id = setObject(notification);
                notification = getObject(id);
            } else {
                notification = readObject(doc.get().get().getData());
            }
            return Collections.singletonList(notification);
        } else {
            return getObjects(q[0], q[1]);
        }
    }

    private List<Notification> getObjects(String s, String s1) {
        return Collections.EMPTY_LIST;
    }
}
