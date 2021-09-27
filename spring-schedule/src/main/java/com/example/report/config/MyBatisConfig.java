package com.example.report.config;

import com.example.report.util.MybatisPaginationInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h3>Detecting MyBatis components</h3>
 * <p> The MyBatis-Spring-Boot-Starter will detects beans that
 * implements following interface provided by MyBatis.
 * <p>Interceptor
 * <p>TypeHandler
 * <p>LanguageDriver (Requires to use together with mybatis-spring 2.0.2+)
 * <p>DatabaseIdProvider
 */
@Configuration
@MapperScan("com.example.report.mapper")
public class MyBatisConfig {

    @Bean
    public MybatisPaginationInterceptor paginationInterceptor() {
        return new MybatisPaginationInterceptor();
    }

    // destroyMethod를 지정 안하면 server를 종료 할때 java.lang.UnsupportedOperationException:
    // Manual close is not allowed over a Spring managed SqlSession 이 WARN으로 로그에 찍힌다.
    @Bean(destroyMethod = "clearCache")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
