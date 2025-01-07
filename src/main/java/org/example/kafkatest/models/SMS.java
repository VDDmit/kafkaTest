package org.example.kafkatest.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SMS {
    @Id
    @UuidGenerator
    private String id;

    @NotNull(message = "Sender phone cannot be null")
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Sender phone must be a valid phone number")
    private String senderPhone;

    @NotNull(message = "Receiver phone cannot be null")
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Receiver phone must be a valid phone number")
    private String receiverPhone;

    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
}