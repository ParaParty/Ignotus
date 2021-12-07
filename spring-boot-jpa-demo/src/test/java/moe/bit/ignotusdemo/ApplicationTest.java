package moe.bit.ignotusdemo;

import moe.bit.ignotusdemo.dao.JpaExampleDao;
import moe.bit.ignotusdemo.model.entity.JpaExampleEntity;
import moe.bit.ignotusdemo.service.implement.JpaExampleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author 13090
 * @version 1.0
 * @description: TODO
 * @date 2021/12/6 10:17
 */
@SpringBootTest
public class ApplicationTest {
    @Resource
    JpaExampleService jpaExampleService;

    @Test
    public void f() {
        System.out.println(jpaExampleService);
        System.out.println("EricNB");
    }

    @Resource
    JpaExampleDao jpaExampleDao;

    @Test
    public void testSave() {
        jpaExampleDao.save(new JpaExampleEntity());
    }

}
