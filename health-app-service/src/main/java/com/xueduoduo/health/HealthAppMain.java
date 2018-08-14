
package com.xueduoduo.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动入口
 * 
 * @author wangzhifeng
 */
@SpringBootApplication
@MapperScan({ "com.xueduoduo.health.dal.dao" })
public class HealthAppMain {

    public static void main(String[] args) {
        SpringApplication.run(HealthAppMain.class, args);
    }

}
