package org.example.kafkatest.services.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kafkatest.models.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();

        kafkaProducerService = new KafkaProducerService(kafkaTemplate, objectMapper);
    }

    @Test
    void sendEmail() throws JsonProcessingException {
        String topic = "email";

        Email email = Email.builder()
                .recipient("test@example.com")
                .subject("Subject")
                .body("Body")
                .build();

        kafkaProducerService.sendEmail(topic, email);

        // Захват отправленного сообщения
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(topic), captor.capture());

        // Преобразуем email в строку с помощью ObjectMapper
        String expectedMessage = objectMapper.writeValueAsString(email);

        // Сравниваем, что отправленное сообщение совпадает с ожидаемым
        assertEquals(expectedMessage, captor.getValue());
    }

    @Test
    void sendPushMessage() {
    }

    @Test
    void sendSMS() {
    }
}