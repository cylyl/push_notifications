package com.github.push.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.push.dao.DeviceDao;
import com.github.push.dao.NotificationDao;
import com.github.push.dao.TopicDao;
import com.github.push.model.Device;
import com.github.push.model.Notification;
import com.github.push.model.Topic;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

//https://github.com/googleapis/java-firestore/blob/bca822734af6e037d90c743ff1ee61e6380de11f/samples/snippets/src/main/java/com/example/firestore/Quickstart.java#L84-L94
public class StoreManager {
    Logger logger = LoggerFactory.getLogger(StoreManager.class);
    final private ObjectMapper objectMapper = new ObjectMapper();
    final private Firestore db;
    final private DeviceDao deviceDao;
    final private TopicDao topicDao;

    public StoreManager() {
        Firestore db = FirestoreClient.getFirestore();
        this.db = db;
        deviceDao = new DeviceDao(db);
        topicDao = new TopicDao(db);
    }

    public Device getDevice(String uuid) throws ExecutionException, InterruptedException, JsonProcessingException {
        return deviceDao.getObject(uuid);
    }

    public void setDevice(Device device) throws ExecutionException, InterruptedException, JsonProcessingException {
        deviceDao.setObject(device);
    }

    public Notification getNotification(Device device, String uuid) throws ExecutionException, InterruptedException, JsonProcessingException {
        NotificationDao notificationDao = getNotificationDao(device);
        return notificationDao.getObject(uuid);
    }

    public void setNotification(Device device, Notification notification) throws ExecutionException, InterruptedException, JsonProcessingException {
        NotificationDao notificationDao = getNotificationDao(device);
        notificationDao.setObject(notification);
        logger.info("Notification (" + notification.getUuid() + ") saved!");
    }

    private NotificationDao getNotificationDao(Device device) {
        return new NotificationDao(
                db, deviceDao.getCollectionName() + "/" + device.getUuid() + "/"
        );
    }

    public void subscribeTopics(List<String> uuids, String topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        Topic topic1 = topicDao.getObject(topic);
        List<String> subscribers = topic1.getSubscribers();
        if (subscribers == null) {
            subscribers = new ArrayList<>();
            topic1.setSubscribers(subscribers);
        }
        List<String> toAdd = new ArrayList<>();
        for (String uuid : uuids) {
            if (!subscribers.contains(uuid)) {
                toAdd.add(uuid);
            }
        }
        topic1.getSubscribers().addAll(toAdd);
        topicDao.setObject(topic1);
    }

    public void unsubscribeTopics(List<String> topics, String uuid) throws ExecutionException, InterruptedException, JsonProcessingException {
        for (String topic : topics) {
            Topic topic1 = topicDao.getObject(topic);
            topic1.getSubscribers().remove(uuid);
            topicDao.setObject(topic1);
        }
    }

    public List<String> getTopics() throws ExecutionException, InterruptedException, JsonProcessingException {
        List<Topic> list = topicDao.getObjects(0, 65535);
        List<String> topics = new ArrayList<>();
        for (Topic topic : list) {
            topics.add(topic.getUuid());
        }
        return topics;
    }

    public Optional<Topic> getTopic(String topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        return Optional.ofNullable(topicDao.getObject(topic));
    }

    public List<Device> getDeviceByToken(String token) throws ExecutionException, InterruptedException, JsonProcessingException {
        return deviceDao.getByToken(token);
    }

    public List<Device> getDeviceByUid(String uid) throws ExecutionException, InterruptedException, JsonProcessingException {
        return deviceDao.getByUid(uid);
    }

    public void DeleteTopicsSubscribers(String topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        topicDao.deleteSubscribers(topic);
    }
}
