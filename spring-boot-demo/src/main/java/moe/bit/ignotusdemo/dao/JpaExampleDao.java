package moe.bit.ignotusdemo.dao;

import moe.bit.ignotusdemo.model.entity.JpaExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Jpa Example Dao
 * @author 13090
 */
@Repository
public interface JpaExampleDao extends JpaRepository<JpaExampleEntity, Long> {


    List<JpaExampleEntity> findAllByName(String name);

    List<JpaExampleEntity> findAllByTheDayBetween(Date begin, Date end);

    List<JpaExampleEntity> findAllByNumberIsGreaterThanEqual(Long number);
}
