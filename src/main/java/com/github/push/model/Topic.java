package com.github.push.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Topic extends Doc {

    @JsonProperty("subscribers")
    private List<String> subscribers;

    public List<String> getSubscribers(){
        return this.subscribers;
    }

    public void setSubscribers(List<String> subscribers){
        this.subscribers = subscribers;
    }

}