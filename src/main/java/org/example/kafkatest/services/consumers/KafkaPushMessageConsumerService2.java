package org.example.kafkatest.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.PushMessage;
import org.example.kafkatest.models.TypeOfNotification;
import org.example.kafkatest.repositories.PushMessageRepository;
import org.example.kafkatest.services.dlt.DLTService;
import org.example.kafkatest.services.stats.NotificationStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})

public class KafkaPushMessageConsumerService2 {

    private final PushMessageRepository pushMessageRepository;
    private final DLTService dltService;
    private final NotificationStatsService notificationStatsService;

    @KafkaListener(topics = "push-messages", groupId = "push-group")
    public void consumePushMessage2(String message) {
        log.info("Consumer 2 processing message: {}", message);
        processMessage(message);
    }

    private void processMessage(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PushMessage pushMessage = objectMapper.readValue(message, PushMessage.class);
            pushMessageRepository.save(pushMessage);
            log.info("Push message saved by Consumer 2: {}", pushMessage);
            notificationStatsService.updateStats(TypeOfNotification.PUSH, true);
        } catch (JsonProcessingException e) {
            log.error("Consumer 2 failed to deserialize message", e);
            dltService.sendToDLT("push-messages", message, e);
            notificationStatsService.updateStats(TypeOfNotification.PUSH, false);
        }
    }
}