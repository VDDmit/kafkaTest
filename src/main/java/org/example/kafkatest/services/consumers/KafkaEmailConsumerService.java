package org.example.kafkatest.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.Email;
import org.example.kafkatest.models.TypeOfNotification;
import org.example.kafkatest.repositories.EmailRepository;
import org.example.kafkatest.services.dlt.DLTService;
import org.example.kafkatest.services.stats.NotificationStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class KafkaEmailConsumerService {

    private final EmailRepository emailRepository;
    private final DLTService dltService;
    private final NotificationStatsService notificationStatsService;

    @KafkaListener(topics = "emails", groupId = "my-group")
    public void consumeEmail(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Email email = objectMapper.readValue(message, Email.class);
            emailRepository.save(email);
            log.info("Email saved to database: {}", email);
            notificationStatsService.updateStats(TypeOfNotification.EMAIL, true);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize email message", e);
            dltService.sendToDLT("email", message, e);
            notificationStatsService.updateStats(TypeOfNotification.EMAIL, false);
        }
    }
}