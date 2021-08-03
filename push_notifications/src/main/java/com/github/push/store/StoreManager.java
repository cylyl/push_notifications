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
import com.google.cloud.firestore.FirestoreOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//https://github.com/googleapis/java-firestore/blob/bca822734af6e037d90c743ff1ee61e6380de11f/samples/snippets/src/main/java/com/example/firestore/Quickstart.java#L84-L94
public class StoreManager {
    Logger logger = LoggerFactory.getLogger(StoreManager.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private Firestore db;
    private DeviceDao deviceDao;
    private TopicDao topicDao;

    public StoreManager() {
        Firestore db = FirestoreOptions.getDefaultInstance().getService();
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

    public void subscribeTopics(List<String> tokens, String topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        Topic topic1 = topicDao.getObject(topic);
        List<String> toAdd = new ArrayList<>();
        for (String token : tokens) {
            if (!topic1.getSubscribers().contains(token)) {
                toAdd.add(token);
            }
        }
        topic1.getSubscribers().addAll(toAdd);
        topicDao.setObject(topic1);
    }

    public void unsubscribeTopics(List<String> topics, String token) throws ExecutionException, InterruptedException, JsonProcessingException {
        for (String topic : topics) {
            Topic topic1 = topicDao.getObject(topic);
            topic1.getSubscribers().remove(token);
            topicDao.setObject(topic1);
        }
    }

    public List<String> getTopics() throws ExecutionException, InterruptedException, JsonProcessingException {
        List<Topic> list = topicDao.getObjects(0, 1000);
        List<String> topics = new ArrayList<>();
        for (Topic topic : list) {
            topics.add(topic.getUuid());
        }
        return topics;
    }
}
