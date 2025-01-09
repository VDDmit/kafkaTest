package org.example.kafkatest.services.stats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkatest.models.NotificationStats;
import org.example.kafkatest.models.TypeOfNotification;
import org.example.kafkatest.repositories.NotificationStatsRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationStatsService {
    private final NotificationStatsRepository statsRepository;

    public NotificationStats getStats(TypeOfNotification type) {
        return statsRepository.findByNotificationType(type)
                .orElse(new NotificationStats(null, type, 0L, 0L, 0L));
    }

    public void updateStats(TypeOfNotification type, boolean success) {
        NotificationStats stats = statsRepository
                .findByNotificationType(type)
                .orElse(new NotificationStats(null, type, 0L, 0L, 0L));
        stats.setTotalSent(stats.getTotalSent() + 1);
        if (success) {
            stats.setSuccessCount(stats.getSuccessCount() + 1);
        } else {
            stats.setFailureCount(stats.getFailureCount() + 1);
        }
        statsRepository.save(stats);
    }
}