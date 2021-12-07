package moe.bit.ignotusdemo;

import moe.bit.ignotusdemo.dao.JpaExampleDao;
import moe.bit.ignotusdemo.model.entity.JpaExampleEntity;
import moe.bit.ignotusdemo.service.implement.JpaExampleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ApplicationTest {

    @Resource
    JpaExampleService jpaExampleService;

    @Resource
    JpaExampleDao jpaExampleDao;

    @Test
    public void f() {
        System.out.println(jpaExampleService);
        System.out.println("EricNB");
    }

    @Test
    public void generateEntityTest() {
        jpaExampleService.generateEntity(10000L);
    }

    @Test
    public void testSave() {
        jpaExampleDao.save(new JpaExampleEntity());
    }

}
