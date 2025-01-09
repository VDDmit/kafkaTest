package org.example.kafkatest.repositories;

import org.example.kafkatest.models.NotificationStats;
import org.example.kafkatest.models.TypeOfNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationStatsRepository extends JpaRepository<NotificationStats, String> {
    Optional<NotificationStats> findByNotificationType(TypeOfNotification notificationType);
}
