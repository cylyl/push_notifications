package com.github.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.model.Notification;
import com.github.push.model.messaging.Message;
import com.github.push.push.Device;
import com.github.push.push.FcmManager;
import com.github.push.push.PubSub;
import com.github.push.push.Push;
import com.github.push.store.StoreManager;
import com.github.push.utils.Mapper;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class PushManager implements Push, PubSub, Device {

    private StoreManager storeManager = new StoreManager();
    private FcmManager fcmManager = new FcmManager();

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

    public int subscribeTopics(String uuid, String topic) {
        return subscribeTopics(Collections.singletonList(getDevice(uuid).getToken()), topic);
    }

    public int unsubscribeTopics(String uuid, String topic) {
        return unsubscribeTopics(Collections.singletonList(getDevice(uuid).getToken()), topic);
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
    public List getTopics(String topic) {
        return null;
    }

    @Override
    public int subscribeTopics(List<String> tokens, String topic) {
        try {
            storeManager.subscribeTopics(tokens, topic);
            return fcmManager.subscribeTopics(tokens, topic);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int unsubscribeTopics(List<String> topics, String token) {
        try {
            storeManager.unsubscribeTopics(topics, token);
            return fcmManager.unsubscribeTopics(topics, token);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public String push(String uuid, com.github.push.model.messaging.Message message) {
        com.github.push.model.Device device = getDevice(uuid);
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
        Notification notification = Mapper.getNotification(message);
        String res = null;
        try {
            res = fcmManager.push(Mapper.mapMessage(message));
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

}
