package com.github.push.push;

import java.util.List;

public interface PubSub {

    /**
     * Retrieve a list of your app's topics and subscribers count.
     *
     * @return list of your app's topics and subscribers
     */
    List getTopics();

    /**
     * Retrieve a list of devices subscribed to a certain topic.
     *
     * @param topic
     * @return list of devices subscribed to a certain topic
     */
    List getTopics(String topic);

    /**
     * Subscribe a device to one or more topics.
     *
     * @param tokens list of topic
     * @param topic  push token
     * @return affected count
     */
    int subscribeTopics(List<String> tokens, String topic);


    /**
     * Unsubscribe a device from one or more topics.
     *
     * @param topics list of topic
     * @param token  push token
     * @return affected count
     */
    int unsubscribeTopics(List<String> topics, String token);
}
