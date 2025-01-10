package org.example.kafkatest.services.handlers;

import lombok.AllArgsConstructor;
import org.example.kafkatest.services.consumers.KafkaEmailConsumerService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailNotificationHandler implements NotificationHandler {
    private final KafkaEmailConsumerService emailService;

    @Override
    public void handle(String message) {
        emailService.consumeEmail(message);
    }
}
