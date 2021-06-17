package com.github.push.model.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebpushFcmOptions {

    @JsonProperty(value = "analytics_label")
    private String analyticsLabel;

    @JsonProperty(value = "link")
    private String link;

    public WebpushFcmOptions(String analyticsLabel, String link) {
        this.analyticsLabel = analyticsLabel;
        this.link = link;
    }

    public String getAnalyticsLabel() {
        return analyticsLabel;
    }

    public String getLink() {
        return link;
    }
}
