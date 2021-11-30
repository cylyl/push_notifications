package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Topic;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TopicDao extends AbstractDao<Topic> {

    public TopicDao(Firestore firestore) {
        super(firestore, Topic.class, "topics");
    }

    @Override
    public List<Topic> getObjects(int offset, int limit) throws ExecutionException, InterruptedException, JsonProcessingException {
        ApiFuture<QuerySnapshot> query = getCollection().offset(offset).limit(limit).get();
        List<Topic> list = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : query.get().getDocuments()) {
            list.add(readObject(snapshot.getData()));
        }
        return list;
    }

    @Override
    public Topic getObject(String id) throws ExecutionException, InterruptedException, JsonProcessingException {
        DocumentReference doc = getCollection().document(id);
        logger.info("[getObject]" + doc.getPath());
        if (!doc.get().get().exists()) {
            Topic topic = new Topic();
            topic.setUuid(id);
            id = setObject(topic);
            return getObject(id);
        } else {
            return readObject(doc.get().get().getData());
        }
    }

    @Override
    public String setObject(Topic topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        logger.info("[setObject] topic...");
        boolean exist = isExist(topic);
        Map<String, Object> map = objectMapper.readValue(objectMapper.writeValueAsString(topic), HashMap.class);
        if (exist) {
            getCollection().document(topic.getUuid()).update(map);
        } else {
            logger.info("Creating topic...");
            if (topic.getUuid() != null) {
                ApiFuture<WriteResult> res = getCollection().document(topic.getUuid()).create(map);
            } else {
                ApiFuture<DocumentReference> doc = getCollection().add(map);
                topic.setUuid(doc.get().getId());
                setObject(topic);
            }
        }
        return topic.getUuid();
    }

    @Override
    public boolean isExist(Topic topic) throws ExecutionException, InterruptedException {
        return topic.getUuid() != null && getCollection().document(topic.getUuid()).get().get().exists();
    }

}
