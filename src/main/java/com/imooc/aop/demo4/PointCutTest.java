package com.imooc.aop.demo4;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PointCutTest {
    @Test
    public void test(){
        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext2.xml");
        CustomDao customDao=(CustomDao)  context.getBean("customDaoProxy");
        customDao.delete();
        customDao.find();
        customDao.save();
        customDao.update();
    }
}
