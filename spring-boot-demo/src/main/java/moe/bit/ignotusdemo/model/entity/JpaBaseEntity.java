package moe.bit.ignotusdemo.model.entity;

import com.tairitsu.ignotus.support.util.JSON;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author A-Salty-Fish
 * @version 1.0
 * @description: base model for jpa
 * @date 2021/11/29 11:14
 */

@MappedSuperclass
public class JpaBaseEntity {
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
