package com.github.push.model.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApnsFcmOptions {

    @JsonProperty(value = "analytics_label")
    private String analyticsLabel;

    @JsonProperty(value = "image")
    private String image;
}
