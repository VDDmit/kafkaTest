package org.example.kafkatest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.Email;
import org.example.kafkatest.models.PushMessage;
import org.example.kafkatest.models.SMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendEmail(String topic, Email email) {
        sendMessage(topic, email, "email");
    }

    public void sendPushMessage(String topic, PushMessage pushMessage) {
        sendMessage(topic, pushMessage, "push message");
    }

    public void sendSMS(String topic, SMS sms) {
        sendMessage(topic, sms, "SMS");
    }

    private void sendMessage(String topic, Object messageObject, String messageType) {
        try {
            String message = objectMapper.writeValueAsString(messageObject);
            log.info("Sending {} to Kafka topic '{}': {}", messageType, topic, message);
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize " + messageType, e);
        }
    }}