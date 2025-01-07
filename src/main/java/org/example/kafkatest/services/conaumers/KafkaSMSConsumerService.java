package org.example.kafkatest.services.conaumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.SMS;
import org.example.kafkatest.repositories.SMSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSMSConsumerService {

    private final SMSRepository smsRepository;

    @Autowired
    public KafkaSMSConsumerService(SMSRepository smsRepository) {
        this.smsRepository = smsRepository;
    }

    @KafkaListener(topics = "sms", groupId = "my-group")
    public void consumeSMS(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SMS sms = objectMapper.readValue(message, SMS.class);
            smsRepository.save(sms);
            log.info("SMS saved to database: {}", sms);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize SMS message", e);
        }
    }
}