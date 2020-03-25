package com.bcht.data_manager.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConnectionFactoryTest {

    @Test
    void test(){
        DruidDataSource dataSource = ConnectionFactory.getDataSource();
        System.out.println(dataSource);
    }
}
