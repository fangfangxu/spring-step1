package com.imooc.aop.demo5;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 基于bean名称的自动代理的创建：BeanNameAutoProxyCreator
 */
public class Test {
    @org.junit.Test
    public void test(){
        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext3.xml");
        TestDao1 testDao1=(TestDao1) context.getBean("testDao1");
        TestDao2 testDao2=(TestDao2) context.getBean("testDao2");
        testDao1.find();
        testDao1.save();
        testDao2.find();
        testDao2.save();



    }

}
