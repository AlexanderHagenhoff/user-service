package com.github.alexanderhagenhoff.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "user-service")
@EntityListeners(AuditingEntityListener.class)
public class User {

    private static final String ID_TYPE = "UUID";

    @Id
    @GeneratedValue(generator = ID_TYPE)
    @GenericGenerator(
            name = ID_TYPE,
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(columnDefinition = ID_TYPE, updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String emailHash;

    @Column(nullable = false, unique = true)
    private String externalSubjectId;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant lastModifiedAt;

    public User() {
    }

    public User(String emailHash, String externalSubjectId) {
        this.emailHash = emailHash;
        this.externalSubjectId = externalSubjectId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public String getExternalSubjectId() {
        return externalSubjectId;
    }

    public void setExternalSubjectId(String externalSubjectId) {
        this.externalSubjectId = externalSubjectId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt.atZone(ZoneId.systemDefault());
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt.toInstant();
    }

    public ZonedDateTime getLastModifiedAt() {
        return lastModifiedAt.atZone(ZoneId.systemDefault());
    }

    public void setLastModifiedAt(ZonedDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt.toInstant();
    }
}
