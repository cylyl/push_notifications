package com.github.push.model.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {


    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "body")
    private String body;

    @JsonProperty(value = "image")
    private String image;


    public Notification() {
        super();
    }

    public Notification(String title, String body, String image) {
        super();
        this.title = title;
        this.body = body;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImage() {
        return image;
    }
}

