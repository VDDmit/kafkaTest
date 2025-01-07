package org.example.kafkatest.services.conaumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.Email;
import org.example.kafkatest.repositories.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class KafkaEmailConsumerService {

    private final EmailRepository emailRepository;

    @Autowired
    public KafkaEmailConsumerService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @KafkaListener(topics = "emails", groupId = "my-group")
    public void consumeEmail(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Email email = objectMapper.readValue(message, Email.class);
            emailRepository.save(email);
            log.info("Email saved to database: {}", email);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize email message", e);
        }
    }
}