package org.example.kafkatest.services.handlers;

import lombok.AllArgsConstructor;
import org.example.kafkatest.services.consumers.KafkaPushMessageConsumerService1;
import org.example.kafkatest.services.consumers.KafkaPushMessageConsumerService2;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PushNotificationHandler implements NotificationHandler {
    private final KafkaPushMessageConsumerService1 pushConsumer1;
    private final KafkaPushMessageConsumerService2 pushConsumer2;

    @Override
    public void handle(String message) {
        if (message.contains("Consumer1")) {
            pushConsumer1.consumePushMessage1(message);
        } else {
            pushConsumer2.consumePushMessage2(message);
        }
    }
}
