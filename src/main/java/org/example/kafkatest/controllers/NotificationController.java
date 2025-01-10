package org.example.kafkatest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.*;
import org.example.kafkatest.services.handlers.NotificationHandler;
import org.example.kafkatest.services.handlers.NotificationHandlerFactory;
import org.example.kafkatest.services.producers.KafkaProducerService;
import org.example.kafkatest.services.stats.NotificationStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationStatsService notificationStatsService;
    private final NotificationHandlerFactory handlerFactory;
    private final KafkaProducerService kafkaProducer;

    @PostMapping("/send/{type}")
    public ResponseEntity<String> sendNotification(@PathVariable TypeOfNotification type, @RequestBody String message) {
        try {
            NotificationHandler handler = handlerFactory.getHandler(type);
            handler.handle(message);
            switch (type) {
                case EMAIL:
                    Email email = new ObjectMapper().readValue(message, Email.class);
                    kafkaProducer.sendEmail("emails", email);
                    break;
                case SMS:
                    SMS sms = new ObjectMapper().readValue(message, SMS.class);
                    kafkaProducer.sendSMS("sms", sms);
                    break;
                case PUSH:
                    PushMessage pushMessage = new ObjectMapper().readValue(message, PushMessage.class);
                    kafkaProducer.sendPushMessage("push-messages", pushMessage);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported notification type: " + type);
            }

            return ResponseEntity.ok("Notification processed and sent to Kafka successfully.");
        } catch (IllegalArgumentException e) {
            log.error("Invalid notification type: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process notification", e);
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<NotificationStats> getNotificationStats(
            @RequestParam TypeOfNotification typeOfNotification) {
        return ResponseEntity.ok(notificationStatsService.getStats(typeOfNotification));
    }
}