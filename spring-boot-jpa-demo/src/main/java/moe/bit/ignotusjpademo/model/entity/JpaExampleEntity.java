package moe.bit.ignotusjpademo.model.entity;

import com.tairitsu.ignotus.database.jpa.model.entity.BaseLongIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author 13090
 * @version 1.0
 * @description: TODO
 * @date 2021/11/29 11:23
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "jpa_example_entity")
@Data
public class JpaExampleEntity extends BaseLongIdEntity {

    @Column(name = "name")
    String name;

    @Column(name = "number")
    Long number;

    @Column(name = "the_day")
    Date theDay;

}
