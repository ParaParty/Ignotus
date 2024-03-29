package moe.bit.ignotusjpademo.dao;

import moe.bit.ignotusjpademo.model.entity.JpaExampleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Jpa Example Dao
 * @author 13090
 */
@Repository
public interface JpaExampleDao extends CrudRepository<JpaExampleEntity, Long> {
    List<JpaExampleEntity> findAllByName(String name);

    List<JpaExampleEntity> findAllByTheDayBetween(Date begin, Date end);

    List<JpaExampleEntity> findAllByNumberGreaterThan(Long number);
}
