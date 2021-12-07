package moe.bit.ignotusdemo.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author 13090
 * @version 1.0
 * @description: Jpa example Dto
 * @date 2021/11/29 20:37
 */
@Data
public class JpaExampleDto {

    public String name;

    public Long number;

    public Date theDay;

}
