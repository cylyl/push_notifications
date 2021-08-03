package com.github.push.push;

import com.google.firebase.messaging.*;

import java.util.Collections;
import java.util.List;

//https://github.com/firebase/firebase-admin-java/blob/70c9668184fa57982da92c0efaf7d901bcd36983/src/test/java/com/google/firebase/snippets/FirebaseMessagingSnippets.java#L46-L60
public class FcmManager {


    private BatchResponse sendAll(List<Message> messages) throws FirebaseMessagingException {
        return FirebaseMessaging.getInstance().sendAll(messages);
    }

    public String push(Message message) throws FirebaseMessagingException {
        return FirebaseMessaging.getInstance().send(message);
    }

    public int subscribeTopics(List<String> tokens, String topic) throws FirebaseMessagingException {
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
                tokens, topic);
        return response.getSuccessCount();
    }

    public int unsubscribeTopics(List<String> topics, String token) throws FirebaseMessagingException {
        int count = 0;
        for (String topic : topics) {
            TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
                    Collections.singletonList(topic), token
            );
            count += response.getSuccessCount();
        }
        return count;
    }
}
