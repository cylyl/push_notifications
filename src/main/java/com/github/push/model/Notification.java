package com.github.push.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Notification extends Doc {

    @JsonProperty("reference_id")
    private String reference_id;
    @JsonProperty("data")
    private Map<String, Object> data;
    @JsonProperty("unread")
    private boolean unread;
    @JsonProperty("created_at")
    private Long created_at;
    @JsonProperty("title")
    private String title;
    @JsonProperty("type")
    private String type;
    @JsonProperty("body")
    private String body;
    @JsonProperty("app_id")
    private Integer app_id;
    @JsonProperty("sender_id")
    private String sender_id;
    @JsonProperty("recipient_id")
    private String recipient_id;

    public String getReference_id(){
        return this.reference_id;
    }

    public void setReference_id(String reference_id){
        this.reference_id = reference_id;
    }

    public Map<String, Object> getData(){
        return this.data;
    }

    public void setData(Map<String, Object> data){
        this.data = data;
    }

    public boolean getUnread(){
        return this.unread;
    }

    public void setUnread(boolean unread){
        this.unread = unread;
    }

    public Long getCreated_at(){
        return this.created_at;
    }

    public void setCreated_at(Long created_at){
        this.created_at = created_at;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getBody(){
        return this.body;
    }

    public void setBody(String body){
        this.body = body;
    }

    public Integer getApp_id(){
        return this.app_id;
    }

    public void setApp_id(Integer app_id){
        this.app_id = app_id;
    }

    public String getSender_id(){
        return this.sender_id;
    }

    public void setSender_id(String sender_id){
        this.sender_id = sender_id;
    }

    public String getRecipient_id(){
        return this.recipient_id;
    }

    public void setRecipient_id(String recipient_id){
        this.recipient_id = recipient_id;
    }

}