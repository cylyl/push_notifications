package com.github.push.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Device;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DeviceDao extends AbstractDao<Device> {

    public DeviceDao(Firestore firestore) {
        super(firestore, Device.class, "devices");
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
    public Device getObject(String id) throws ExecutionException, InterruptedException, JsonProcessingException {
        DocumentReference doc = getCollection().document(id);
        logger.info("[getObject]" + doc.getPath());
        if (!doc.get().get().exists()) {
            Device device = new Device();
            device.setUuid(id);
            id = setObject(device);
            return getObject(id);
        } else {
            return readObject(doc.get().get().getData());
        }
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
        return device.getUuid();
    }

    @Override
    public boolean isExist(Device device) throws ExecutionException, InterruptedException {
        return device.getUuid() != null && getCollection().document(device.getUuid()).get().get().exists();
    }

    public Device getByToken(String token) throws ExecutionException, InterruptedException, JsonProcessingException {
        return get("token", token);
    }

    public Device getByUid(String uid) throws ExecutionException, InterruptedException, JsonProcessingException {
        return get("uid", uid);
    }

    private Device get(String w, String v) throws ExecutionException, InterruptedException, JsonProcessingException {
        Query query = getCollection().whereEqualTo(w, v);
        ApiFuture<QuerySnapshot> snap = query.get();
        for (QueryDocumentSnapshot queryDocumentSnapshot : snap.get().getDocuments()
        ) {
            return readObject(queryDocumentSnapshot.getData());
        }
        return null;
    }
}
