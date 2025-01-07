package org.example.kafkatest.repositories;

import org.example.kafkatest.models.PushMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushMessageRepository extends JpaRepository<PushMessage, Long> {
}
