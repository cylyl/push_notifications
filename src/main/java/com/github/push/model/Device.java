package com.github.push.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device extends Doc {

    @JsonProperty("token")
    private String token;


    public String getToken(){
        return this.token;
    }

    public void setToken(String token){
        this.token = token;
    }

}