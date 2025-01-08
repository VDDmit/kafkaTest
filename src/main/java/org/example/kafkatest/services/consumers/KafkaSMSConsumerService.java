package org.example.kafkatest.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.SMS;
import org.example.kafkatest.repositories.SMSRepository;
import org.example.kafkatest.services.dlt.DLTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class KafkaSMSConsumerService {

    private final SMSRepository smsRepository;
    private final DLTService dltService;

    @KafkaListener(topics = "sms", groupId = "my-group")
    public void consumeSMS(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SMS sms = objectMapper.readValue(message, SMS.class);
            smsRepository.save(sms);
            log.info("SMS saved to database: {}", sms);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize SMS message", e);
            dltService.sendToDLT("sms", message, e);
        }
    }
}