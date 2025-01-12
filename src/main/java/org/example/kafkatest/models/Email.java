package org.example.kafkatest.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
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
public class Email {
    @Id
    @UuidGenerator
    private String id;

    @NotNull(message = "Recipient cannot be null")
    @jakarta.validation.constraints.Email(message = "Recipient must be a valid email address")
    private String recipient;

    @NotNull(message = "Subject cannot be null")
    @Size(max = 255, message = "Subject cannot exceed 255 characters")
    private String subject;

    @Size(max = 1000, message = "Body cannot exceed 1000 characters")
    private String body;
}