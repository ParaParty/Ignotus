package moe.bit.ignotusjpademo.model.vo;

import com.tairitsu.ignotus.serializer.vo.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.bit.ignotusjpademo.model.entity.JpaExampleEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * @author 13090
 * @version 1.0
 * @description: Jpa Example VO
 * @date 2021/11/29 20:40
 */

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
@Data
public class JpaExampleVo extends BaseResponse {

    public String name;

    public Long number;

    public Date theDay;

    public JpaExampleVo(JpaExampleEntity jpaExampleEntity) {
        this.setName(jpaExampleEntity.getName());
        this.setNumber(jpaExampleEntity.getNumber());
        this.setTheDay(jpaExampleEntity.getTheDay());
    }

    @NotNull
    @Override
    public String getModelType() {
        return this.getClass().getSimpleName();
    }

    @NotNull
    @Override
    public String getId() {
        return String.valueOf(this.hashCode());
    }
}
