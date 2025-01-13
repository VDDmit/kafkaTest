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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaPushMessageConsumerService1Test {

    @Mock
    private PushMessageRepository pushMessageRepository;
    @Mock
    private DLTService dltService;
    @Mock
    private NotificationStatsService notificationStatsService;
    @InjectMocks
    private KafkaPushMessageConsumerService1 kafkaPushMessageConsumerService1;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void consumePushMessage1_success() throws JsonProcessingException {
        PushMessage pushMessage = PushMessage.builder().
                message("Message")
                .topic("Topic")
                .build();
        String pushMessageJson = objectMapper.writeValueAsString(pushMessage);
        kafkaPushMessageConsumerService1.consumePushMessage1(pushMessageJson);
        verify(pushMessageRepository).save(eq(pushMessage));
        verify(notificationStatsService).updateStats(TypeOfNotification.PUSH, true);
        verifyNoInteractions(dltService);
    }

    @Test
    void consumePushMessage1_failure() {
        // Создаем некорректное JSON-сообщение
        String invalidMessage = "{ invalid json }";

        // Вызываем тестируемый метод
        kafkaPushMessageConsumerService1.consumePushMessage1(invalidMessage);

        // Проверяем, что сообщение не сохраняется
        verify(pushMessageRepository, never()).save(any());

        // Захватываем параметры вызова DLTService
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(dltService).sendToDLT(topicCaptor.capture(), messageCaptor.capture(), exceptionCaptor.capture());

        // Проверяем корректность переданных в DLTService аргументов
        assertEquals("push-messages", topicCaptor.getValue());
        assertEquals(invalidMessage, messageCaptor.getValue());
        assertNotNull(exceptionCaptor.getValue());
        assertInstanceOf(JsonProcessingException.class, exceptionCaptor.getValue());

        // Проверяем, что статистика обновлена с флагом ошибки
        verify(notificationStatsService).updateStats(TypeOfNotification.PUSH, false);
    }
}