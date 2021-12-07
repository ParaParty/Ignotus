package moe.bit.ignotusjpademo;

import moe.bit.ignotusjpademo.dao.JpaExampleDao;
import moe.bit.ignotusjpademo.model.entity.JpaExampleEntity;
import moe.bit.ignotusjpademo.service.implement.JpaExampleService;
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
