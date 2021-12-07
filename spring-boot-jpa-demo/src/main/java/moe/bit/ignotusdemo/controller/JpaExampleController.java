package moe.bit.ignotusdemo.controller;

import moe.bit.ignotusdemo.model.dto.JpaExampleDto;
import moe.bit.ignotusdemo.model.entity.JpaExampleEntity;
import moe.bit.ignotusdemo.model.vo.JpaExampleVo;
import moe.bit.ignotusdemo.service.implement.JpaExampleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 13090
 * @version 1.0
 * @description: TODO
 * @date 2021/11/29 20:58
 */


@RestController
@RequestMapping("example")
public class JpaExampleController {

    @Resource
    JpaExampleService jpaExampleService;

    @GetMapping("number/{number}")
    public Map<String, Long> getDateCountMapByNumberGreaterThan(@PathVariable("number") Long number) {
        return jpaExampleService.getDateCountMapByNumberGreaterThan(number);
    }

    @GetMapping("name")
    Map<String, JpaExampleVo> getMapByName(@RequestParam("name") String name) {
        return jpaExampleService.getMapByName(name)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> new JpaExampleVo(entry.getValue())));
    }

    @PostMapping("date")
    Double getAvgNumberByDateBetween(@RequestBody JpaExampleDto jpaExampleDto) {
        Date inputDate = jpaExampleDto.getTheDay();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(inputDate);
        calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR)+1);
        Date oneYearAfter= calendar.getTime();
        return jpaExampleService.getAvgNumberByDateBetween(inputDate, oneYearAfter);
    }

    @PutMapping("")
    boolean generateEntity(@RequestParam("number") Long number) {
        return jpaExampleService.generateEntity(number);
    }
}
