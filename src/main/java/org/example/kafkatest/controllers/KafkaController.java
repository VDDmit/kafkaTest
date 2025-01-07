package org.example.kafkatest.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.services.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public KafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String topic, @RequestParam String message) {
        log.info("Received request to send message to topic '{}'", topic);
        kafkaProducerService.sendMessage(topic, message);
        log.info("Message '{}' sent to topic '{}'", message, topic);
        return "Message sent to topic: " + topic;
    }
}