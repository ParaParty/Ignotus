package moe.bit.ignotusdemo.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

/**
 * @author 13090
 * @version 1.0
 * @description: TODO
 * @date 2021/11/29 11:23
 */
@Entity
@Data
public class JpaExampleEntity extends JpaBaseEntity {

    String name;

    Long number;

    Date theDay;

}
