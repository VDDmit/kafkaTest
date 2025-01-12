package org.example.kafkatest.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationStats {
    @Id
    @UuidGenerator
    private String id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeOfNotification notificationType; // EMAIL, SMS, PUSH

    @NotNull
    private Long totalSent; // Общее количество отправленных

    @NotNull
    private Long successCount; // Успешные отправки

    @NotNull
    private Long failureCount; // Ошибочные отправки
}
