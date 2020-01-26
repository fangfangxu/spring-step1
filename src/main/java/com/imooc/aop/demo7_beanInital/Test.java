package com.imooc.aop.demo7_beanInital;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
    @org.junit.Test
    public void test(){
        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext5.xml");
        Student stu=(Student)context.getBean("student");
        stu.business();
        ((ClassPathXmlApplicationContext) context).close();
    }
}
