package moe.bit.ignotusdemo.service.implement;

import moe.bit.ignotusdemo.dao.JpaExampleDao;
import moe.bit.ignotusdemo.model.entity.JpaExampleEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 13090
 * @version 1.0
 * @description: TODO
 * @date 2021/11/29 21:00
 */

@Service
public class JpaExampleService {

    @Resource
    JpaExampleDao jpaExampleDao;

    /**
     * get map by entity name without null
     *
     * @param name input
     * @return Map<String, JpaExampleEntity>
     */
    public Map<String, JpaExampleEntity> getMapByName(String name) {
        return jpaExampleDao.findAllByName(name)
            .parallelStream()
            .filter(entity -> entity.getName() != null)
            .collect(Collectors.toMap(JpaExampleEntity::getName, entity -> entity));
    }

    /**
     * get average number between begin date and end date
     *
     * @param begin begin date
     * @param end   end date
     * @return average number
     */
    public Double getAvgNumberByDateBetween(Date begin, Date end) {
        return jpaExampleDao.findAllByTheDayBetween(begin, end)
            .stream()
            .filter(entity -> entity.getNumber() != null)
            .mapToLong(JpaExampleEntity::getNumber)
            .average()
            .orElse(0.0);
    }


    /**
     * get entity count which has the same day date and greater than the input number
     *
     * @param number input
     * @return Map<String, Long>
     */
    public Map<String, Long> getDateCountMapByNumberGreaterThan(Long number) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return jpaExampleDao.findAllByNumberGreaterThan(number)
            .stream()
            .filter(entity -> entity.getTheDay() != null)
            .collect(Collectors.groupingBy(entity -> simpleDateFormat.format(entity.getTheDay()), Collectors.counting()));
    }

}
