package org.example.kafkatest.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.NotificationStats;
import org.example.kafkatest.models.TypeOfNotification;
import org.example.kafkatest.services.handlers.NotificationHandler;
import org.example.kafkatest.services.handlers.NotificationHandlerFactory;
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


    @PostMapping("/send/{type}")
    public ResponseEntity<String> sendNotification(@PathVariable TypeOfNotification type, @RequestBody String message) {
        try {
            NotificationHandler handler = handlerFactory.getHandler(type);
            handler.handle(message);
            return ResponseEntity.ok("Notification processed successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<NotificationStats> getNotificationStats(
            @RequestParam TypeOfNotification typeOfNotification) {
        return ResponseEntity.ok(notificationStatsService.
                getStats(typeOfNotification));
    }


}
