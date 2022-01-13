package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Topic;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class TopicDao extends AbstractDao<Topic> {

    public TopicDao(Firestore firestore, int cacheExpireAfterAccess, int cacheMaximumSize) {
        super(firestore, Topic.class, "topics"
                , cacheExpireAfterAccess, cacheMaximumSize);
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
        List<Topic> list = cache.getUnchecked("uuid:" + id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public String setObject(Topic topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        cache.refresh("uuid:" + topic.getUuid());
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

    public void deleteSubscribers(String topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        Topic topic1 = getObject(topic);
        topic1.setSubscribers(new ArrayList<>());
        setObject(topic1);
    }

    @Override
    public List<Topic> load(String id) throws Exception {
        String[] q = id.split(":");
        if (id.startsWith("uuid:")) {
            Topic topic;
            DocumentReference doc = getCollection().document(q[1]);
            logger.info("[getObject]" + doc.getPath());
            if (!doc.get().get().exists()) {
                topic = new Topic();
                topic.setUuid(id);
                id = setObject(topic);
                topic = getObject(id);
            } else {
                topic = readObject(doc.get().get().getData());
            }
            return Collections.singletonList(topic);
        } else {
            return getObjects(q[0], q[1]);
        }
    }

    private List<Topic> getObjects(String s, String s1) {
        return Collections.EMPTY_LIST;
    }
}
