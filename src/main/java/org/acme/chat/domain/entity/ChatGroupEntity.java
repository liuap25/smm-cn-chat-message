package org.acme.chat.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_groups")
@Getter
@Setter
public class ChatGroupEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "psychologist_id", nullable = false)
    private String psychologistId;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    
}
