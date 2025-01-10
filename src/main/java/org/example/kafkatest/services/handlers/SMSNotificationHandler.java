package org.example.kafkatest.services.handlers;

import lombok.AllArgsConstructor;
import org.example.kafkatest.services.consumers.KafkaSMSConsumerService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SMSNotificationHandler implements NotificationHandler {
    private final KafkaSMSConsumerService kafkaSMSConsumerService;

    @Override
    public void handle(String message) {
        kafkaSMSConsumerService.consumeSMS(message);
    }
}
