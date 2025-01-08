package org.example.kafkatest.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.Email;
import org.example.kafkatest.repositories.EmailRepository;
import org.example.kafkatest.services.dlt.DLTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class KafkaEmailConsumerService {

    private final EmailRepository emailRepository;
    private final DLTService dltService;


    @Autowired
    public KafkaEmailConsumerService(EmailRepository emailRepository, DLTService dltService) {
        this.emailRepository = emailRepository;
        this.dltService = dltService;
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
            dltService.sendToDLT("email",message,e);
        }
    }
}