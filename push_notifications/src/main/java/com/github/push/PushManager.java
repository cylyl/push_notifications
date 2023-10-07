package com.github.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Notification;
import com.github.push.model.Topic;
import com.github.push.model.messaging.Message;
import com.github.push.push.Device;
import com.github.push.push.FcmManager;
import com.github.push.push.PubSub;
import com.github.push.push.Push;
import com.github.push.store.StoreManager;
import com.github.push.utils.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


public class PushManager implements Push, PubSub, Device {

    Logger logger = LoggerFactory.getLogger(PushManager.class);

    final private StoreManager storeManager = new StoreManager(
            1, 65535
    );
    final private FcmManager fcmManager = new FcmManager();

    public PushManager() {
    }

    public com.github.push.model.Device getDevice(String uuid) {
        try {
            return storeManager.getDevice(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<com.github.push.model.Device> getDeviceByUid(String uid) {
        try {
            return storeManager.getDeviceByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateToken(String uuid, String token) throws ExecutionException, InterruptedException, JsonProcessingException {
        com.github.push.model.Device device = storeManager.getDevice(uuid);
        device.setToken(token);
        storeManager.setDevice(device);
    }

    public int subscribeTopic(List<String> uuids, String topic) {
        return subscribeTopics(uuids, topic);
    }

    public int subscribeTopic(String uuid, String topic) {
        return subscribeTopics(Collections.singletonList(uuid), topic);
    }

    public int unsubscribeTopics(String uuid, String topic) {
        return unsubscribeTopics(Collections.singletonList(topic), uuid);
    }

    public int subscribeTopicUid(String uid, String topic) {
        List<com.github.push.model.Device> deviceList = getDeviceByUid(uid);
        List<String> uuids = new ArrayList<>();
        for (com.github.push.model.Device device : deviceList) {
            Optional<Topic> optionalTopic;
            try {
                optionalTopic = getTopic(topic);
                if (optionalTopic.isPresent()) {
                    List<String> list = optionalTopic.get().getSubscribers();
                    if (list == null) {
                        list = new java.util.ArrayList<>();
                    }
                    if (!list.contains(device.getUuid())) {
                        uuids.add(device.getUuid());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (uuids.isEmpty()) {
            return 0;
        }
        return subscribeTopics(uuids, topic);
    }

    public int unsubscribeTopicUid(String uid, String topic) {
        List<com.github.push.model.Device> deviceList = getDeviceByUid(uid);
        int count = 0;
        for (com.github.push.model.Device device : deviceList) {
            Optional<Topic> optionalTopic;
            try {
                optionalTopic = getTopic(topic);
                if (optionalTopic.isPresent()) {
                    List<String> list = optionalTopic.get().getSubscribers();
                    if (list == null) {
                        list = new java.util.ArrayList<>();
                    }
                    if (list.contains(device.getUuid())) {
                        count += unsubscribeTopics(Collections.singletonList(topic), device.getUuid());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    @Override
    public List<String> getTopics() throws ExecutionException, InterruptedException, JsonProcessingException {
        return storeManager.getTopics();
    }

    @Override
    public List<String> getTopics(String topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        Optional<Topic> opt = getTopic(topic);
        if (opt.isPresent()) {
            return opt.get().getSubscribers();
        }
        return new ArrayList<>(0);
    }

    public Optional<Topic> getTopic(String topic) throws ExecutionException, InterruptedException, JsonProcessingException {
        return storeManager.getTopic(topic);
    }

    @Override
    public int subscribeTopics(List<String> uuids, String topic) {
        try {
            storeManager.subscribeTopics(uuids, topic);
            return uuids.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int subscribeTopics(String uuid, List<String> topics) {
        int count = 0;
        for (String topic : topics
        ) {
            count += subscribeTopic(uuid, topic);
        }
        return count;
    }

    private List<String> getToken(List<String> uuids) {
        List<String> tokens = new ArrayList<>();
        for (String uuid : uuids
        ) {
            String token = getDevice(uuid).getToken();
            if (token != null) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    @Override
    public int unsubscribeTopics(List<String> topics, String uuid) {
        try {
            storeManager.unsubscribeTopics(topics, uuid);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public String push(com.github.push.model.Device device,
                       com.github.push.model.messaging.Message message) {
        if (!device.getToken().equals(message.getToken())) {
            message = new Message(
                    message.getName(),
                    message.getData(),
                    message.getNotification(),
                    message.getAndroid(),
                    device.getToken(),
                    message.getTopic(),
                    message.getCondition(),
                    message.getFcmOptions(),
                    message.getWebpushConfig()
            );
        }

        String res = null;
        try {
            res = fcmManager.push(Mapper.mapMessage(message));
            Notification notification = Mapper.getNotification(message);
            storeManager.setNotification(device, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String push(String uuid, com.github.push.model.messaging.Message message) {
        com.github.push.model.Device device = getDevice(uuid);
        return push(device, message);
    }

    String pushSimpleMessage(String uuid, String title, String body) {
        final Message message = new Message(
                null,
                null,
                new com.github.push.model.messaging.Notification(title, body, null),
                /*AndroidConfig*/ null,
                /*String token*/ null,
                /*String topic*/ null,
                null,
                null,
                null
        );
        return push(uuid, message);
    }

    public void pushSimpleMessageToTopic(String topic, String title, String body) throws ExecutionException, InterruptedException, JsonProcessingException {
        Optional<Topic> opt = getTopic(topic);
        if (!opt.isPresent()) {
            return;
        }
        Topic topic1 = opt.get();
        final Message message = new Message(
                null,
                null,
                new com.github.push.model.messaging.Notification(title, body, null),
                /*AndroidConfig*/ null,
                /*String token*/ null,
                /*String topic*/ topic,
                null,
                null,
                null
        );

        for (String uuid : topic1.getSubscribers()
        ) {
            com.github.push.model.Device device = getDevice(uuid);
            String res = push(device, message);
            logger.debug(res);
        }
    }


    public void unsubscribeAll() throws ExecutionException, InterruptedException, JsonProcessingException {
        storeManager.getTopics().forEach(topic -> {
            try {
                storeManager.DeleteTopicsSubscribers(topic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
