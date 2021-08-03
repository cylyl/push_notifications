package com.github.push.utils;

import io.github.sharelison.jsontojava.JsonToJava;

public class GenModel {
    static JsonToJava jsonToJava = new JsonToJava();

    public static void main(String[] args) {
        /**
         * db.collection("devices/123/notifications").get();
         *             logger.info(objectMapper.writeValueAsString(snap.get().getDocuments().get(0).getData()));
         */
//        jsonToJava(
//                "Topic",
//                "{\"name\":\"news\",\"subscribers\":[\"a3bc47808cfd31c1a66517\",\"40f3073bb387e3829d01f6\"]}"
//        );
//        jsonToJava(
//                "Notification",
//                "{\"reference_id\":\"\",\"data\":{\"a\":\"a\",\"b\":1},\"unread\":true,\"created_at\":{\"seconds\":1623772800,\"nanos\":0},\"title\":\"\",\"type\":\"\",\"body\":\"body\",\"app_id\":0,\"sender_id\":\"\",\"recipient_id\":\"\"}"
//        );
//        jsonToJava(
//                "Device",
//                " {\"uuid\":\"123\",\"token\":\"\"}"
//        );


    }

    private static void jsonToJava(String name, String json) {
        jsonToJava.jsonToJava(
                json,
                name,
                "com.github.push.model",
                "./src/main/java/com/github/push/model/"
        );
    }
}
