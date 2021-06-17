package com.github.push.push;

import com.github.push.model.messaging.Message;

public interface Push {

    /**
     * Send push notifications to your users.
     */
    String push(String uuid, Message message);

}
