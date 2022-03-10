package com.kanseiu.accumulation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: kanseiu
 * @Date: 2022-03-01 15:10
 **/
@SpringBootApplication
@ComponentScan(basePackages = {"com.kanseiu"})
@MapperScan("com.kanseiu.accumulation")
public class AccumulationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccumulationApplication.class, args);
    }

}
