package org.example.kafkatest.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kafkatest.models.PushMessage;
import org.example.kafkatest.models.TypeOfNotification;
import org.example.kafkatest.repositories.PushMessageRepository;
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

class KafkaPushMessageConsumerService2Test {

    @Mock
    private PushMessageRepository pushMessageRepository;

    @Mock
    private DLTService dltService;

    @Mock
    private NotificationStatsService notificationStatsService;

    @InjectMocks
    private KafkaPushMessageConsumerService2 kafkaPushMessageConsumerService2;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void consumePushMessage2_success() throws JsonProcessingException {
        PushMessage pushMessage = PushMessage.builder()
                .message("Test Message")
                .topic("Test Topic")
                .build();
        String pushMessageJson = objectMapper.writeValueAsString(pushMessage);

        kafkaPushMessageConsumerService2.consumePushMessage2(pushMessageJson);

        // Проверяем сохранение в репозиторий
        verify(pushMessageRepository).save(pushMessage);

        // Проверяем обновление статистики
        verify(notificationStatsService).updateStats(TypeOfNotification.PUSH, true);

        // Проверяем, что DLTService не вызывался
        verifyNoInteractions(dltService);
    }

    @Test
    void consumePushMessage2_failure() {
        String invalidMessage = "{ invalid json }";

        kafkaPushMessageConsumerService2.consumePushMessage2(invalidMessage);

        // Проверяем, что PushMessage не сохранялся
        verify(pushMessageRepository, never()).save(any());

        // Проверяем вызов DLTService с ошибкой
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(dltService).sendToDLT(topicCaptor.capture(), messageCaptor.capture(), exceptionCaptor.capture());

        assertEquals("push-messages", topicCaptor.getValue());
        assertEquals(invalidMessage, messageCaptor.getValue());
        assertNotNull(exceptionCaptor.getValue());

        // Проверяем, что статистика обновлена с флагом ошибки
        verify(notificationStatsService).updateStats(TypeOfNotification.PUSH, false);
    }
}