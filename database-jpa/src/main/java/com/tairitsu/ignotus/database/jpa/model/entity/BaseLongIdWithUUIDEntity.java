package com.tairitsu.ignotus.database.jpa.model.entity;

import com.tairitsu.ignotus.support.util.JSON;
import com.tairitsu.ignotus.support.util.UUIDUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * base long id model with uuid for jpa
 *
 * @author A-Salty-Fish
 * @version 1.0
 */

@MappedSuperclass
public class BaseLongIdWithUUIDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private String uuid = UUIDUtils.createNoLeadingDigitId();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseLongIdWithUUIDEntity that = (BaseLongIdWithUUIDEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(uuid, that.uuid) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, createdAt, updatedAt);
    }
}
