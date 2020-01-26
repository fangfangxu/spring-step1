package com.imooc.aop.demo6;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MyAroundAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
      System.out.println("环绕增强前###############");
      Object proxy =  methodInvocation.proceed();
      System.out.println("环绕增强后###############");
      return proxy;
    }
}
