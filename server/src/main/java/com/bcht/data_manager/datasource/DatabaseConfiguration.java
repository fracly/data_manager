package com.bcht.data_manager.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

/**
 * data base configuration
 */
@Configuration
@PropertySource({"classpath:data_source.properties"})
@MapperScan(basePackages = "com.bcht.data_manager.mapper", sqlSessionFactoryRef = "SqlSessionFactory")
public class DatabaseConfiguration {

    /**
     * register data source
     */
    @Primary
    @Bean(name = "DataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource() {
        return ConnectionFactory.getDataSource();
    }

    @Primary
    @Bean(name = "SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());

        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "TransactionManager")
    public PlatformTransactionManager transactionManager() throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }
}
