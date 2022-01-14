package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Tab;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class TabDao extends AbstractDao<Tab> {

    public TabDao(Firestore firestore, int cacheExpireAfterAccess, int cacheMaximumSize) {
        super(firestore, Tab.class, "tabs"
                , cacheExpireAfterAccess, cacheMaximumSize);
    }

    @Override
    public List<Tab> getObjects(int offset, int limit) throws ExecutionException, InterruptedException, JsonProcessingException {
        ApiFuture<QuerySnapshot> query = getCollection().offset(offset).limit(limit).get();
        List<Tab> list = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : query.get().getDocuments()) {
            list.add(readObject(snapshot.getData()));
        }
        return list;
    }

    @Override
    public Tab getObject(String id) throws ExecutionException, InterruptedException, JsonProcessingException {
        List<Tab> list = cache.getUnchecked("uuid:" + id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public String setObject(Tab tab) throws ExecutionException, InterruptedException, JsonProcessingException {
        cache.refresh("uuid:" + tab.getUuid());
        logger.info("[setObject] tab...");
        boolean exist = isExist(tab);
        Map<String, Object> map = objectMapper.readValue(objectMapper.writeValueAsString(tab), HashMap.class);
        if (exist) {
            getCollection().document(tab.getUuid()).update(map);
        } else {
            logger.info("Creating tab...");
            if (tab.getUuid() != null) {
                ApiFuture<WriteResult> res = getCollection().document(tab.getUuid()).create(map);
            } else {
                ApiFuture<DocumentReference> doc = getCollection().add(map);
                tab.setUuid(doc.get().getId());
                setObject(tab);
            }
        }
        return tab.getUuid();
    }

    @Override
    public boolean isExist(Tab tab) throws ExecutionException, InterruptedException {
        return tab.getUuid() != null && getCollection().document(tab.getUuid()).get().get().exists();
    }

    @Override
    public List<Tab> load(String id) throws Exception {
        String[] q = id.split(":");
        if (id.startsWith("uuid:")) {
            id = q[1];
            Tab tab;
            DocumentReference doc = getCollection().document(id);
            logger.info("[getObject]" + doc.getPath());
            if (!doc.get().get().exists()) {
                tab = new Tab();
                tab.setUuid(id);
                id = setObject(tab);
                tab = getObjects("uuid", id).get(0);
            } else {
                tab = readObject(doc.get().get().getData());
            }
            return Collections.singletonList(tab);
        } else {
            return getObjects(q[0], q[1]);
        }
    }
}
