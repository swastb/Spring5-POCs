package com.async.springmvc;

import java.util.Properties;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "com.async.springmvc")
@EnableJpaRepositories("com.async.springmvc.service") 
@EnableTransactionManagement

public class HelloWorldConfiguration {
    @Bean
    public DriverManagerDataSource conferenceDataSource () {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(EmbeddedDriver.class.getName());
        dataSource.setUrl("jdbc:derby:conference;create=true");

//        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        dataSource.setUrl("jdbc:sqlserver://jcaps-db-prod;instanceName=JCAPS;database=jcaps_mapping_Q");
//        dataSource.setUsername("jcaps");
//        dataSource.setPassword("jcaps");

        return dataSource;
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
        emFactory.setDataSource((conferenceDataSource()));
        emFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        emFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "create");
        emFactory.setJpaProperties(jpaProperties);
        emFactory.setPackagesToScan(this.getClass().getPackage().getName());
        return emFactory;
    }
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

}