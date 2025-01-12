package org.example.kafkatest.services.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kafkatest.models.Email;
import org.example.kafkatest.models.PushMessage;
import org.example.kafkatest.models.SMS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    void sendPushMessage() throws JsonProcessingException {
        String topic = "push-messages";
        PushMessage pushMessage = PushMessage.builder().
                message("Message")
                .topic("Topic")
                .build();
        kafkaProducerService.sendPushMessage(topic, pushMessage);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(topic), captor.capture());

        String expectedMessage = objectMapper.writeValueAsString(pushMessage);
        assertEquals(expectedMessage, captor.getValue());
    }

    @Test
    void sendSMS() throws JsonProcessingException {
        String topic = "sms";
        SMS sms = SMS.builder()
                .message("Massage")
                .receiverPhone("+89092159999")
                .senderPhone("+89092152299")
                .build();
        kafkaProducerService.sendSMS(topic, sms);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(topic), captor.capture());

        String expectedMessage = objectMapper.writeValueAsString(sms);
        assertEquals(expectedMessage, captor.getValue());
    }

    @Test
    void testSendMessageThrowsException() throws JsonProcessingException {
        String topic = "email";
        Email email = Email.builder()
                .recipient("test@example.com")
                .subject("Subject")
                .body("Body")
                .build();

        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        when(mockObjectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Test Exception") {
        });

        KafkaProducerService faultyService = new KafkaProducerService(kafkaTemplate, mockObjectMapper);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                faultyService.sendEmail(topic, email)
        );

        assertTrue(exception.getMessage().contains("Failed to serialize email"));
    }
}