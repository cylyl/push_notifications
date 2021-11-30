package com.github.push.utils;

import com.github.push.model.messaging.ApnsAlert;
import com.github.push.model.messaging.ApnsPayload;
import com.google.firebase.messaging.*;

public class Mapper {

    public static Message mapMessage(com.github.push.model.messaging.Message message) {
        Message.Builder builder = Message.builder();
        builder.setAndroidConfig(Mapper.mapAndroidConfig(message.getAndroid()));
        builder.setApnsConfig(Mapper.mapApnsConfig(message.getApns()));
        builder.setCondition(message.getCondition());
        builder.setNotification(Mapper.mapNotification(message.getNotification()));
        builder.setToken(message.getToken());
        builder.setFcmOptions(Mapper.mapFcmOptions(message.getFcmOptions()));
        builder.setWebpushConfig(Mapper.mapWebpush(message.getWebpushConfig()));
        builder.setTopic(message.getTopic());
        if (message.getData() != null) builder.putAllData(message.getData());
        return builder.build();
    }

    private static WebpushConfig mapWebpush(com.github.push.model.messaging.WebpushConfig webpushConfig) {
        if (webpushConfig == null) return null;
        WebpushConfig.Builder builder = WebpushConfig.builder();
        builder.setFcmOptions(Mapper.mapWebpushConfig(webpushConfig.getWebpushFcmOptions()));
        builder.setNotification(Mapper.mapWebpushNotification(webpushConfig.getNotification()));
        return builder.build();
    }

    private static WebpushNotification mapWebpushNotification(com.github.push.model.messaging.Notification notification) {
        if (notification == null) return null;
        WebpushNotification.Builder builder = WebpushNotification.builder();
        builder.setBody(notification.getBody());
        builder.setTitle(notification.getTitle());
        builder.setImage(notification.getImage());
        return builder.build();
    }

    private static WebpushFcmOptions mapWebpushConfig(com.github.push.model.messaging.WebpushFcmOptions webpushFcmOptions) {
        if (webpushFcmOptions == null) return null;
        WebpushFcmOptions.Builder builder = WebpushFcmOptions.builder();
        builder.setLink(webpushFcmOptions.getLink());
        return builder.build();
    }

    private static FcmOptions mapFcmOptions(com.github.push.model.messaging.FcmOptions fcmOptions) {
        if (fcmOptions == null) return null;
        FcmOptions.Builder builder = FcmOptions.builder();
        builder.setAnalyticsLabel(fcmOptions.getAnalyticsLabel());
        return builder.build();
    }

    private static Notification mapNotification(com.github.push.model.messaging.Notification notification) {
        if (notification == null) return null;
        Notification.Builder builder = Notification.builder();
        builder.setBody(notification.getBody());
        builder.setImage(notification.getImage());
        builder.setTitle(notification.getTitle());
        return builder.build();
    }

    private static ApnsConfig mapApnsConfig(com.github.push.model.messaging.ApnsConfig apns) {
        if (apns == null) return null;
        ApnsConfig.Builder builder = ApnsConfig.builder();
        builder.setAps(Mapper.mapAps(apns.getPayload()));
        builder.putAllHeaders(apns.getHeaders());
        return builder.build();
    }

    private static Aps mapAps(ApnsPayload payload) {
        if (payload == null) return null;
        Aps.Builder builder = Aps.builder();
        builder.setAlert(Mapper.mapApsAlert(payload.getAlert()));
        builder.setBadge(Integer.parseInt(payload.getBadge()));
        builder.setCategory(payload.getCategory());
        builder.setContentAvailable(Boolean.parseBoolean(payload.getContentAvailable()));
//        builder.setMutableContent(payload.)
        builder.setSound(payload.getSound());
        builder.setThreadId(payload.getThreadId());
        return builder.build();
    }

    private static ApsAlert mapApsAlert(ApnsAlert alert) {
        if (alert == null) return null;
        ApsAlert.Builder builder = ApsAlert.builder();
        builder.setBody(alert.getBody());
        builder.setTitle(alert.getTitle());
        builder.setLaunchImage(alert.getLaunchImage());
//        builder.setSubtitle(alert.getSubtitle());
        builder.setLocalizationKey(alert.getLocKey());
        builder.addAllLocalizationArgs(alert.getLocArgs());
//        builder.setSubtitleLocalizationKey()
//        builder.addSubtitleLocalizationArg()
        builder.setActionLocalizationKey(alert.getActionLocKey());
        builder.setTitleLocalizationKey(alert.getTitleLocKey());
        builder.addAllSubtitleLocArgs(alert.getTitleLocArgs());

        return null;
    }

    private static AndroidConfig mapAndroidConfig(com.github.push.model.messaging.AndroidConfig android) {
        if (android == null) return null;
        AndroidConfig.Builder builder = AndroidConfig.builder();
        return builder.build();
    }

    public static com.github.push.model.Notification getNotification(com.github.push.model.messaging.Message message) {
        com.github.push.model.Notification notification = new com.github.push.model.Notification();
        notification.setBody(message.getNotification().getBody());
        notification.setTitle(message.getNotification().getTitle());
        notification.setUnread(true);
        notification.setType(message.getTopic());
        notification.setCreated_at(System.currentTimeMillis());
        return notification;
    }
}
