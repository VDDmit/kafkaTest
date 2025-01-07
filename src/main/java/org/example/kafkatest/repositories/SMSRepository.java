package org.example.kafkatest.repositories;

import org.example.kafkatest.models.SMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMSRepository extends JpaRepository<SMS, String> {
}
