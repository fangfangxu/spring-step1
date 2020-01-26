package com.imooc.aop.demo6;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
    @org.junit.Test
    public void test(){
        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext4.xml");
        Custom c2=(Custom) context.getBean("customDao");
        User u1=(User) context.getBean("userDao");
        c2.find();
        c2.save();
        u1.find();
        u1.save();
    }
}
