package com.github.push.model.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages#Notification
 *
 * @author user
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Message {

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "data")
    private Map<String, String> data;

    @JsonProperty(value = "notification")
    private Notification notification;

    @JsonProperty(value = "android")
    private AndroidConfig android;

    @JsonProperty(value = "apns")
    private ApnsConfig apns;

    @JsonProperty(value = "token")
    private String token;

    @JsonProperty(value = "topic")
    private String topic;

    @JsonProperty(value = "condition")
    private String condition;

    @JsonProperty(value = "fcm_options")
    private FcmOptions fcmOptions;

    @JsonProperty(value = "webpush")
    private WebpushConfig webpushConfig;

    public Message() {
        super();
    }

    public Message(String name, Map<String, String> data, Notification notification, AndroidConfig android,
                   String token, String topic, String condition, FcmOptions fcmOptions, WebpushConfig webpushConfig) {
        super();
        this.name = name;
        this.data = data;
        this.notification = notification;
        this.android = android;
        this.token = token;
        this.topic = topic;
        this.condition = condition;
        this.fcmOptions = fcmOptions;
        this.webpushConfig = webpushConfig;

    }

    public String getName() {
        return name;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Notification getNotification() {
        return notification;
    }

    public AndroidConfig getAndroid() {
        return android;
    }

    public String getToken() {
        return token;
    }

    public String getTopic() {
        return topic;
    }

    public String getCondition() {
        return condition;
    }

    public ApnsConfig getApns() {
        return apns;
    }


    public FcmOptions getFcmOptions() {
        return fcmOptions;
    }

    public WebpushConfig getWebpushConfig() {
        return webpushConfig;
    }
}
