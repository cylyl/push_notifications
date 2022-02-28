package com.github.push.push;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.ExecutionException;

public interface Device {

    /**
     * Retrieve device info, presence, pending notifications, etc.
     *
     * @param token Push token
     */
    com.github.push.model.Device getDevice(String token);

    /**
     * Update device push token.
     *
     * @param uuid  Device UUID
     * @param token Push token
     */
    void updateToken(String uuid, String token) throws ExecutionException, InterruptedException, JsonProcessingException;
}
