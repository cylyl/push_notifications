package com.github.push.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device extends Doc {

    @JsonProperty("token")
    private String token;

    @JsonProperty("uid")
    private String uid;


    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}