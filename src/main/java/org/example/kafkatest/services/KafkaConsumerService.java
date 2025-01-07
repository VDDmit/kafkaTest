package org.example.kafkatest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.Email;
import org.example.kafkatest.models.PushMessage;
import org.example.kafkatest.models.SMS;
import org.example.kafkatest.repositories.EmailRepository;
import org.example.kafkatest.repositories.PushMessageRepository;
import org.example.kafkatest.repositories.SMSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {

    private final EmailRepository emailRepository;
    private final PushMessageRepository pushMessageRepository;
    private final SMSRepository smsRepository;

    @Autowired
    public KafkaConsumerService(EmailRepository emailRepository,
                                PushMessageRepository pushMessageRepository,
                                SMSRepository smsRepository) {
        this.emailRepository = emailRepository;
        this.pushMessageRepository = pushMessageRepository;
        this.smsRepository = smsRepository;
    }

    @KafkaListener(topics = "emails", groupId = "my-group")
    public void consumeEmail(String message) {
        consumeMessage(message, Email.class, emailRepository);
    }

    @KafkaListener(topics = "push-message", groupId = "my-group")
    public void consumePushMessage(String message) {
        consumeMessage(message, PushMessage.class, pushMessageRepository);
    }

    @KafkaListener(topics = "sms", groupId = "my-group")
    public void consumeSMS(String message) {
        consumeMessage(message, SMS.class, smsRepository);
    }

    private <T> void consumeMessage(String message, Class<T> type, JpaRepository<T, ?> repository) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            T object = objectMapper.readValue(message, type);
            repository.save(object);
            log.info("{} saved to database: {}", type.getSimpleName(), object);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message", e);
        }
    }


}