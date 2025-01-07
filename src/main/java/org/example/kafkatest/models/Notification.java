package org.example.kafkatest.models;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.UuidGenerator;

@MappedSuperclass
public abstract class Notification {
    @Id
    @UuidGenerator
    private String id;

    private String message;
}