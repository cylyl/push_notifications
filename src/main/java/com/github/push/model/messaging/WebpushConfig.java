package com.github.push.model.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WebpushConfig {

    @JsonProperty(value = "headers")
    private Map<String, String> headers;

    @JsonProperty(value = "data")
    private Map<String, String> data;

    @JsonProperty(value = "notification")
    private Notification notification;

    @JsonProperty(value = "fcm_options")
    private WebpushFcmOptions webpushFcmOptions;

    public WebpushConfig(Map<String, String> headers, Map<String, String> data, Notification notification, WebpushFcmOptions webpushFcmOptions) {
        this.headers = headers;
        this.data = data;
        this.notification = notification;
        this.webpushFcmOptions = webpushFcmOptions;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public WebpushFcmOptions getWebpushFcmOptions() {
        return webpushFcmOptions;
    }

    public Notification getNotification() {
        return notification;
    }
}
