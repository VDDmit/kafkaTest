package org.example.kafkatest.services.dlt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DLTService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public DLTService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToDLT(String originalTopic, String message, Exception exception) {
        try {
            String errorMessage = String.format(
                    "{\"originalTopic\": \"%s\", \"message\": \"%s\", \"error\": \"%s\"}",
                    originalTopic, message, exception.getMessage()
            );
            kafkaTemplate.send("errors-dlt", errorMessage);
            log.info("Message sent to DLT: {}", errorMessage);
        } catch (Exception e) {
            log.error("Failed to send message to DLT. Original message: {}, Error: {}", message, e.getMessage(), e);
        }
    }
}
