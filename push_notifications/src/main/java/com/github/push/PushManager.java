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
import com.google.firebase.messaging.FirebaseMessagingException;
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
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<com.github.push.model.Device> getDeviceByUid(String uid) {
        try {
            return storeManager.getDeviceByUid(uid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateToken(String uuid, String token) {
        try {
            com.github.push.model.Device device = storeManager.getDevice(uuid);
            device.setToken(token);
            storeManager.setDevice(device);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
            Optional<Topic> optionalTopic = getTopic(topic);
            if (optionalTopic.isPresent()) {
                List<String> list = optionalTopic.get().getSubscribers();
                if (list == null) {
                    list = new java.util.ArrayList<>();
                }
                if (list.contains(device.getUuid())) {
                } else {
                    uuids.add(device.getUuid());
                }

            }
        }
        return subscribeTopics(uuids, topic);
    }

    public int unsubscribeTopicUid(String uid, String topic) {
        List<com.github.push.model.Device> deviceList = getDeviceByUid(uid);
        int count = 0;
        for (com.github.push.model.Device device : deviceList) {
            Optional<Topic> optionalTopic = getTopic(topic);
            if (optionalTopic.isPresent()) {
                List<String> list = optionalTopic.get().getSubscribers();
                if (list == null) {
                    list = new java.util.ArrayList<>();
                }
                if (list.contains(device.getUuid())) {
                    count += unsubscribeTopics(Collections.singletonList(topic), device.getUuid());
                } else {
                }

            }
        }
        return count;
    }

    @Override
    public List<String> getTopics() {
        try {
            return storeManager.getTopics();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public List<String> getTopics(String topic) {
        Optional<Topic> opt = getTopic(topic);
        if (opt.isPresent()) {
            return opt.get().getSubscribers();
        }
        return new ArrayList();
    }

    public Optional<Topic> getTopic(String topic) {
        try {
            return storeManager.getTopic(topic);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int subscribeTopics(List<String> uuids, String topic) {
        try {
            storeManager.subscribeTopics(uuids, topic);
            return uuids.size();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
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
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public String push(com.github.push.model.Device device,
                       com.github.push.model.messaging.Message message) {
        if (device.getToken().equals(message.getToken()) == false) {
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
            if (notification != null) {
                storeManager.setNotification(device, notification);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (FirebaseMessagingException e) {
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

    public void pushSimpleMessageToTopic(String topic, String title, String body) throws FirebaseMessagingException {
        Optional<Topic> opt = getTopic(topic);
        if (opt.isPresent() == false) {
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
        String res = fcmManager.push(Mapper.mapMessage(message));
        logger.debug(res);
        try {
            Notification notification = Mapper.getNotification(message);
                for (String uuid : topic1.getSubscribers()
                ) {
                    com.github.push.model.Device device = getDevice(uuid);
                    try {
                        storeManager.setNotification(device, notification);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public void unsubscribeAll() throws ExecutionException, InterruptedException, JsonProcessingException {
        storeManager.getTopics().forEach(topic -> {
            try {
                storeManager.DeleteTopicsSubscribers(topic);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
