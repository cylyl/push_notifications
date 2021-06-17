package com.github.push.push;

public interface Device {

    /**
     * Retrieve device info, presence, pending notifications, etc.
     * @param token
     */
    public com.github.push.model.Device getDevice(String token);

    /**
     * Update device push token.
     * @param uuid
     * @param token
     */
    public void updateToken(String uuid, String token);
}
