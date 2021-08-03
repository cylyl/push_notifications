package com.github.push.model.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FcmOptions {
    public String getAnalyticsLabel() {
        return analyticsLabel;
    }

    @JsonProperty(value = "analytics_label")
    private String analyticsLabel;

    public FcmOptions(String analyticsLabel) {
        this.analyticsLabel = analyticsLabel;
    }
}
