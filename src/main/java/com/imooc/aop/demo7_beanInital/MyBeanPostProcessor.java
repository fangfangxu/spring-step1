package com.imooc.aop.demo7_beanInital;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        System.out.println("步骤五：存在类实现BeanPostProcessor，执行前处理方法");
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        System.out.println("步骤八：存在类实现BeanPostProcessor，执行后处理方法");
        return o;
    }
}
