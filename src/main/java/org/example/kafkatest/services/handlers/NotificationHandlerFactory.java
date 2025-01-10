package org.example.kafkatest.services.handlers;

import org.example.kafkatest.models.TypeOfNotification;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class NotificationHandlerFactory {
    private final Map<TypeOfNotification, NotificationHandler> handlers = new EnumMap<>(TypeOfNotification.class);

    public NotificationHandlerFactory(EmailNotificationHandler emailHandler,
                                      SMSNotificationHandler smsHandler,
                                      PushNotificationHandler pushHandler) {
        handlers.put(TypeOfNotification.EMAIL, emailHandler);
        handlers.put(TypeOfNotification.SMS, smsHandler);
        handlers.put(TypeOfNotification.PUSH, pushHandler);
    }

    public NotificationHandler getHandler(TypeOfNotification type) {
        NotificationHandler handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("No handler found for notification type: " + type);
        }
        return handler;
    }
}
