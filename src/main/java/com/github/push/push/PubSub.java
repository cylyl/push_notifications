package com.github.push.push;

import java.util.List;

public interface PubSub {

    /**
     * Retrieve a list of your app's topics and subscribers count.
     * @return
     */
    public List getTopics();

    /**
     * Retrieve a list of devices subscribed to a certain topic.
     * @param topic
     * @return
     */
    public List getTopics(String topic);

    /**
     * Subscribe a device to one or more topics.
     * @param tokens
     * @param topic
     * @return
     */
    public int subscribeTopics(List<String> tokens,  String topic);


    /**
     * Unsubscribe a device from one or more topics.
     * @param topics
     * @param token
     * @return
     */
    public int unsubscribeTopics(List<String> topics,  String token);
}
