package org.example.kafkatest.services.conaumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.PushMessage;
import org.example.kafkatest.repositories.PushMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaPushMessageConsumerService2 {

    private final PushMessageRepository pushMessageRepository;

    @Autowired
    public KafkaPushMessageConsumerService2(PushMessageRepository pushMessageRepository) {
        this.pushMessageRepository = pushMessageRepository;
    }

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
        } catch (JsonProcessingException e) {
            log.error("Consumer 2 failed to deserialize message", e);
        }
    }
}