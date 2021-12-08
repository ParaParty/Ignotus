package com.tairitsu.ignotus.database.jpa.model.entity;

import com.tairitsu.ignotus.support.util.JSON;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * base model for jpa
 *
 * @author A-Salty-Fish
 * @version 1.0
 */

@MappedSuperclass
public class BaseLongIdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "create_time", insertable = false, updatable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "update_time", insertable = false, updatable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'")
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
