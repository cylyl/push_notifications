package com.github.push.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Doc {

    @JsonProperty("uuid")
    private String uuid;

    public String getUuid(){
        return this.uuid;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

}
