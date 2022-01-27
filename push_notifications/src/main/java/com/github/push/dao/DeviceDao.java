package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Device;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class DeviceDao extends AbstractDao<Device> {

    public DeviceDao(Firestore firestore, int cacheExpireAfterAccess, int cacheMaximumSize) {
        super(firestore, Device.class, "devices"
                , cacheExpireAfterAccess, cacheMaximumSize);
    }

    @Override
    public List<Device> getObjects(int offset, int limit) throws ExecutionException, InterruptedException, JsonProcessingException {
        ApiFuture<QuerySnapshot> query = getCollection().offset(offset).limit(limit).get();
        List<Device> list = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : query.get().getDocuments()) {
            list.add(readObject(snapshot.getData()));
        }
        return list;
    }


    @Override
    public String setObject(Device device) throws ExecutionException, InterruptedException, JsonProcessingException {
        logger.info("[setObject] device...");
        boolean exist = isExist(device);
        Map<String, Object> map = objectMapper.readValue(objectMapper.writeValueAsString(device), HashMap.class);
        if (exist) {
            getCollection().document(device.getUuid()).update(map);
        } else {
            logger.info("Creating device...");
            if (device.getUuid() != null) {
                ApiFuture<WriteResult> res = getCollection().document(device.getUuid()).create(map);
            } else {
                ApiFuture<DocumentReference> doc = getCollection().add(map);
                device.setUuid(doc.get().getId());
                setObject(device);
            }
        }
        cache.refresh("uuid:" + device.getUuid());
        return device.getUuid();
    }

    @Override
    public boolean isExist(Device device) throws ExecutionException, InterruptedException {
        return device.getUuid() != null && getCollection().document(device.getUuid()).get().get().exists();
    }

    public List<Device> getByToken(String token) throws ExecutionException, InterruptedException, JsonProcessingException {
        return cache.get("token:" + token);
    }

    public List<Device> getByUid(String uid) throws ExecutionException, InterruptedException, JsonProcessingException {
        return cache.get("uid:" + uid);
    }




    @Override
    public List<Device> load(String id) throws Exception {
        String[] q = id.split(":");
        if (id.startsWith("uuid:")) {
            id = q[1];
            Device device;
            DocumentReference doc = getCollection().document(id);
            logger.info("[getObject]" + doc.getPath());
            if (!doc.get().get().exists()) {
                device = new Device();
                device.setUuid(id);
                id = setObject(device);
                device = getObjects("uuid", id).get(0);
            } else {
                device = readObject(doc.get().get().getData());
            }
            return Collections.singletonList(device);
        } else {
            return getObjects(q[0], q[1]);
        }
    }
}
