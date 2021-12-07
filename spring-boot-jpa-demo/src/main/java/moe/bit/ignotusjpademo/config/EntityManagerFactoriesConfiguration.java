package moe.bit.ignotusjpademo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

/**
 * @author 13090
 * @version 1.0
 * @description: TODO
 * @date 2021/12/7 19:40
 */

@Configuration
public class EntityManagerFactoriesConfiguration {
    @Autowired
    private DataSource dataSource;

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean emf() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan(
            "moe.bit.ignotusjpademo.model.entity");
        emf.setJpaVendorAdapter(
            new HibernateJpaVendorAdapter());
        return emf;
    }
}
