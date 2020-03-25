/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bcht.data_manager.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.mapper.UserMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import javax.sql.DataSource;

import static com.bcht.data_manager.utils.PropertyUtils.*;

/**
 * data source connection factory
 */
public class ConnectionFactory {
  private static SqlSessionFactory sqlSessionFactory;

  /**
   * get the data source
   */
  public static DruidDataSource getDataSource() {
    DruidDataSource druidDataSource = new DruidDataSource();

    druidDataSource.setDriverClassName(getString(Constants.SPRING_DATASOURCE_DRIVER_CLASS_NAME));
    druidDataSource.setUrl(getString(Constants.SPRING_DATASOURCE_URL));
    druidDataSource.setUsername(getString(Constants.SPRING_DATASOURCE_USERNAME));
    druidDataSource.setPassword(getString(Constants.SPRING_DATASOURCE_PASSWORD));
    druidDataSource.setValidationQuery(getString(Constants.SPRING_DATASOURCE_VALIDATION_QUERY));

    return druidDataSource;
  }

  /**
   * get sql session factory
   */
  public static SqlSessionFactory getSqlSessionFactory() {
    if (sqlSessionFactory == null) {
      synchronized (ConnectionFactory.class) {
        if (sqlSessionFactory == null) {
          DataSource dataSource = getDataSource();
          TransactionFactory transactionFactory = new JdbcTransactionFactory();

          Environment environment = new Environment(Constants.DEVELOPMENT, transactionFactory, dataSource);

          Configuration configuration = new Configuration(environment);
          configuration.setLazyLoadingEnabled(true);

          configuration.addMappers(UserMapper.class.getPackage().getName());

          SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
          sqlSessionFactory = builder.build(configuration);
        }
      }
    }

    return sqlSessionFactory;
  }

  /**
   * get sql session
   */
  public static SqlSession getSqlSession() {
    return new SqlSessionTemplate(getSqlSessionFactory());
  }

  public static <T> T getMapper(Class<T> type){
    return getSqlSession().getMapper(type);
  }
}
