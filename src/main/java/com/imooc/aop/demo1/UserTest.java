package com.imooc.aop.demo1;

import org.junit.Test;

public class UserTest {
    @Test
    public void test1(){
        UserDao userDao=new UserDaoImpl();
        MyJdkProxy myJdkProxy=new MyJdkProxy(userDao);
        UserDao proxy=(UserDao)myJdkProxy.createProxy();
        proxy.delete();
        proxy.find();
        proxy.save();
        proxy.update();


    }
}
