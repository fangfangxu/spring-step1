package com.imooc.aop.demo7_beanInital;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

public class Student implements BeanNameAware, BeanFactoryAware
, InitializingBean, DisposableBean {
    //姓名
    private String name;
    public Student(){
        System.out.println("步骤一：执行构造方法");
    }

   public void setName(String name){
       System.out.println("步骤二：设置属性");
       this.name=name;
   }

    @Override
    public void setBeanName(String s) {
        System.out.println("步骤三：实现BeanNameAware，执行setBeanName");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("步骤四：实现BeanFactoryAware，执行setBeanFactory");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("步骤六：实现InitializingBean，执行afterPropertiesSet");
    }

    public void myinit(){
        System.out.println("步骤七：执行init-method中指定的myinit");
    }

    public void mydestroy(){
        System.out.println("步骤十一：执行destroy-method中指定的mydestroy");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("步骤十：实现DisposableBean，执行destroy");
    }

    public void business(){
        System.out.println("步骤九：自身业务方法");
    }
}
