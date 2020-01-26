package com.imooc.aop.demo1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理
 */
public class MyJdkProxy implements InvocationHandler {
    public MyJdkProxy(UserDao userDao) {
        this.userDao = userDao;
    }

    private UserDao userDao;

    public Object createProxy() {
        Object proxy = Proxy.newProxyInstance(userDao.getClass().getClassLoader()
                , userDao.getClass().getInterfaces(),
                this);
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("save".equals(method.getName())) {
            System.out.println("****jdk动态代理save增强****");
            method.invoke(userDao, args);
        }
        return method.invoke(userDao, args);
    }
}
