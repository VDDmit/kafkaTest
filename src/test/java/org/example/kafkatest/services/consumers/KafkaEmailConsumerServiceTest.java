package org.example.kafkatest.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kafkatest.models.Email;
import org.example.kafkatest.models.TypeOfNotification;
import org.example.kafkatest.repositories.EmailRepository;
import org.example.kafkatest.services.dlt.DLTService;
import org.example.kafkatest.services.stats.NotificationStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaEmailConsumerServiceTest {

    @Mock
    private EmailRepository emailRepository;
    @Mock
    private DLTService dltService;
    @Mock
    private NotificationStatsService notificationStatsService;
    @InjectMocks
    private KafkaEmailConsumerService kafkaEmailConsumerService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void consumeEmail_success() throws JsonProcessingException {
        Email email = Email.builder()
                .recipient("test@example.com")
                .subject("Subject")
                .body("Body")
                .build();
        String emailJson = objectMapper.writeValueAsString(email);

        kafkaEmailConsumerService.consumeEmail(emailJson);

        verify(notificationStatsService).updateStats(TypeOfNotification.EMAIL, true);
        verifyNoInteractions(dltService);
    }

    @Test
    void consumeEmail_failure() {
        String invalidMessage = "{ invalid json }";
        kafkaEmailConsumerService.consumeEmail(invalidMessage);

        // Проверяем, что email не был сохранен
        verify(emailRepository, never()).save(any());

        // Проверяем, что DLTService был вызван с корректными аргументами
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(dltService).sendToDLT(topicCaptor.capture(), messageCaptor.capture(), exceptionCaptor.capture());
        assertEquals("email", topicCaptor.getValue());
        assertEquals(invalidMessage, messageCaptor.getValue());
        assertNotNull(exceptionCaptor.getValue());


        verify(notificationStatsService).updateStats(TypeOfNotification.EMAIL, false);
    }
}