package com.github.push.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.push.PushManager;
import com.github.push.model.messaging.Message;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@RestController
public class FcmController {


    Logger logger = LoggerFactory.getLogger(FcmController.class);


    @PostMapping("/pushmsg")
    public ResponseEntity pushmsg(
            HttpServletRequest request,
            @RequestParam(name = "token") String token,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "text") String text
    ) throws FirebaseMessagingException, ExecutionException, InterruptedException, JsonProcessingException {

        if(token != null && pushManager != null)
            pushManager.pushSimpleMessageToTopic(
                    "Messages", title, text
            );
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    public ResponseEntity push(
            HttpServletRequest request,
            @RequestParam(name = "uuid") String uuid,
            @RequestBody Message message
    ) throws FirebaseMessagingException {
        return new ResponseEntity<>(pushManager.push(uuid, message), HttpStatus.OK);
    }

    @GetMapping("/device")
    public ResponseEntity getDevices(
            @RequestParam(name = "uuid") String uuid
    ) {
        return new ResponseEntity<>(pushManager.getDevice(uuid), HttpStatus.OK);
    }

    @GetMapping("/topics")
    public ResponseEntity getTopics(
    ) throws ExecutionException, InterruptedException, JsonProcessingException {
        return new ResponseEntity<>(pushManager.getTopics(), HttpStatus.OK);
    }

    @PutMapping("/topic/subscribe")
    public ResponseEntity subscribeTopics(
            @RequestParam(name = "uuid") String uuid,
            @RequestParam(name = "topic") String topic
    ) {
        return new ResponseEntity<>(pushManager.subscribeTopic(uuid, topic), HttpStatus.OK);
    }

    @PutMapping("/topic/unsubscribe")
    public ResponseEntity unsubscribeTopics(
            @RequestParam(name = "uuid") String uuid,
            @RequestParam(name = "topic") String topic
    ) {
        return new ResponseEntity<>(pushManager.unsubscribeTopics(uuid, topic), HttpStatus.OK);
    }


    @Autowired
    PushManager pushManager;
}
