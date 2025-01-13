package org.example.kafkatest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kafkatest.models.Email;
import org.example.kafkatest.models.NotificationStats;
import org.example.kafkatest.models.SMS;
import org.example.kafkatest.models.TypeOfNotification;
import org.example.kafkatest.services.handlers.NotificationHandler;
import org.example.kafkatest.services.handlers.NotificationHandlerFactory;
import org.example.kafkatest.services.producers.KafkaProducerService;
import org.example.kafkatest.services.stats.NotificationStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationStatsService notificationStatsService;

    @MockitoBean
    private NotificationHandlerFactory handlerFactory;

    @MockitoBean
    private KafkaProducerService kafkaProducer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSendNotification_Email_Success() throws Exception {
        String emailMessage = """
                {
                    "recipient": "test@example.com",
                    "subject": "Test Subject",
                    "body": "Test Body"
                }
                """;

        NotificationHandler mockHandler = mock(NotificationHandler.class);
        when(handlerFactory.getHandler(TypeOfNotification.EMAIL)).thenReturn(mockHandler);

        mockMvc.perform(post("/api/notifications/send/EMAIL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailMessage))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification processed and sent to Kafka successfully."));

        verify(mockHandler).handle(emailMessage);
        verify(kafkaProducer).sendEmail(eq("emails"), any(Email.class));
    }

    @Test
    void testSendNotification_SMS_ValidData() throws Exception {
        String validSMSMessage = """
                {
                    "senderPhone": "+1234567890",
                    "receiverPhone": "+0987654321",
                    "message": "Test SMS message."
                }
                """;

        NotificationHandler mockHandler = mock(NotificationHandler.class);
        when(handlerFactory.getHandler(TypeOfNotification.SMS)).thenReturn(mockHandler);

        mockMvc.perform(post("/api/notifications/send/SMS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSMSMessage))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification processed and sent to Kafka successfully."));

        verify(mockHandler).handle(validSMSMessage);
        verify(kafkaProducer).sendSMS(eq("sms"), any(SMS.class));
    }

    @Test
    void testSendNotification_SMS_InvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/notifications/send/SMS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request format for sms notification."));
    }

    @Test
    void testSendNotification_Push_MissingFields() throws Exception {
        String invalidPushMessage = """
                {
                    "message": "This is a test push message."
                }
                """;

        mockMvc.perform(post("/api/notifications/send/PUSH")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPushMessage))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request format for push notification."));
    }

    @Test
    void testSendNotification_Email_InvalidData() throws Exception {
        String invalidEmailMessage = """
                {
                    "recipient": "invalid-email",
                    "subject": "",
                    "body": "This body is fine."
                }
                """;

        mockMvc.perform(post("/api/notifications/send/EMAIL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailMessage))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request format for email notification."));

        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void testSendNotification_SMS_Success() throws Exception {
        String smsMessage = """
                {
                    "receiverPhone": "+1234567890",
                    "message": "Test SMS Message"
                }
                """;

        NotificationHandler mockHandler = mock(NotificationHandler.class);
        when(handlerFactory.getHandler(TypeOfNotification.SMS)).thenReturn(mockHandler);

        mockMvc.perform(post("/api/notifications/send/SMS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(smsMessage))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification processed and sent to Kafka successfully."));

        verify(mockHandler).handle(smsMessage);
        verify(kafkaProducer).sendSMS(eq("sms"), any(SMS.class));
    }

    @Test
    void testGetNotificationStats() throws Exception {
        NotificationStats stats = NotificationStats.builder()
                .notificationType(TypeOfNotification.PUSH)
                .totalSent(12L)
                .successCount(10L)
                .failureCount(2L)
                .build();

        when(notificationStatsService.getStats(TypeOfNotification.PUSH)).thenReturn(stats);

        mockMvc.perform(get("/api/notifications/stats")
                        .param("typeOfNotification", "PUSH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSent").value(12))
                .andExpect(jsonPath("$.successCount").value(10))
                .andExpect(jsonPath("$.failureCount").value(2));
    }

    @Test
    void testGetNotificationStats_ValidType() throws Exception {
        NotificationStats stats = NotificationStats.builder()
                .notificationType(TypeOfNotification.EMAIL)
                .totalSent(50L)
                .successCount(45L)
                .failureCount(5L)
                .build();

        when(notificationStatsService.getStats(TypeOfNotification.EMAIL)).thenReturn(stats);

        mockMvc.perform(get("/api/notifications/stats")
                        .param("typeOfNotification", "EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationType").value("EMAIL"))
                .andExpect(jsonPath("$.totalSent").value(50))
                .andExpect(jsonPath("$.successCount").value(45))
                .andExpect(jsonPath("$.failureCount").value(5));
    }

    @Test
    void testGetNotificationStats_MissingType() throws Exception {
        mockMvc.perform(get("/api/notifications/stats"))
                .andExpect(status().isBadRequest());
    }
}