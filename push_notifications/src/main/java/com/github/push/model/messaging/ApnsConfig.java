package com.github.push.model.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApnsConfig {

    @JsonProperty(value = "headers")
    private Map<String, String> headers;

    @JsonProperty(value = "payload")
    private ApnsPayload payload;

    @JsonProperty(value = "fcm_options")
    private ApnsFcmOptions apnsFcmOptions;

    public ApnsConfig(Map<String, String> headers, ApnsPayload payload, ApnsFcmOptions apnsFcmOptions) {
        super();
        this.headers = headers;
        this.payload = payload;
        this.apnsFcmOptions = apnsFcmOptions;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ApnsPayload getPayload() {
        return payload;
    }


    public ApnsFcmOptions getApnsFcmOptions() {
        return apnsFcmOptions;
    }
}
