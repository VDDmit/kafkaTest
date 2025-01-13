package org.example.kafkatest.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kafkatest.models.SMS;
import org.example.kafkatest.models.TypeOfNotification;
import org.example.kafkatest.repositories.SMSRepository;
import org.example.kafkatest.services.dlt.DLTService;
import org.example.kafkatest.services.stats.NotificationStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class KafkaSMSConsumerServiceTest {

    @Mock
    private SMSRepository smsRepository;
    @Mock
    private DLTService dltService;
    @Mock
    private NotificationStatsService notificationStatsService;
    @InjectMocks
    private KafkaSMSConsumerService kafkaSMSConsumerService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void consumeSMS_success() throws JsonProcessingException {
        SMS sms = SMS.builder()
                .message("Massage")
                .receiverPhone("+89092159999")
                .senderPhone("+89092152299")
                .build();
        String smsJson = objectMapper.writeValueAsString(sms);

        kafkaSMSConsumerService.consumeSMS(smsJson);
        verify(notificationStatsService).updateStats(TypeOfNotification.SMS, true);
        verifyNoInteractions(dltService);
    }

    @Test
    void consumeSMS_failure(){
        String invalidMessage = "{ invalid json }";
        kafkaSMSConsumerService.consumeSMS(invalidMessage);

        // Проверяем, что sms не был сохранен
        verify(smsRepository, never()).save(any());

        // Проверяем, что DLTService был вызван с корректными аргументами
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(dltService).sendToDLT(topicCaptor.capture(), messageCaptor.capture(), exceptionCaptor.capture());
        assertEquals("sms", topicCaptor.getValue());
        assertEquals(invalidMessage, messageCaptor.getValue());
        assertNotNull(exceptionCaptor.getValue());

        verify(notificationStatsService).updateStats(TypeOfNotification.SMS, false);
    }
}