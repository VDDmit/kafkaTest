package org.example.kafkatest.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PushMessage {
    @Id
    @UuidGenerator
    private String id;

    @NotNull(message = "Topic cannot be null")
    @Size(max = 100, message = "Topic cannot exceed 100 characters")
    private String topic;

    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
}