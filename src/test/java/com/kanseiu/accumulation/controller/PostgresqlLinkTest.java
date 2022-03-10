package com.kanseiu.accumulation.controller;

import com.kanseiu.accumulation.AccumulationApplication;
import com.kanseiu.accumulation.mapper.Test1Mapper;
import com.kanseiu.accumulation.model.Test1;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: kanseiu
 * @Date: 2022-03-03 18:30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccumulationApplication.class)
public class PostgresqlLinkTest {

    @Resource
    private Test1Mapper test1Mapper;

    @Test
    public void test1(){
        Test1 test1 = new Test1();
        test1Mapper.insert(test1);
    }

}
