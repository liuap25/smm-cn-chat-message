package org.acme.chat.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessageEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_group_id", nullable = false)
    private ChatGroupEntity chatGroup;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String receiverId; 

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant sentAt;

    private Instant readAt;


    
}
