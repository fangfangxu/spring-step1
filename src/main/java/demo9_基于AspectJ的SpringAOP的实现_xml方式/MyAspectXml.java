package demo9_基于AspectJ的SpringAOP的实现_xml方式;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 切面类
 */
public class MyAspectXml {
   //前置通知
    public void before(JoinPoint joinPoint){
        System.out.println("XML方式的前置通知================="+joinPoint);
    }

    //后置通知
    public void after(Object result){
        System.out.println("XML方式的后置通知================="+result);
    }

    //环绕通知
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("XML方式的环绕前=================");
        Object o=joinPoint.proceed();
        System.out.println("XML方式的环绕后=================");
        return o;
    }

    //异常通知
    public void throwing(Throwable e){
        System.out.println("XML方式的异常通知================="+e.getMessage());
    }
    
    //最终通知
    public void after(){
        System.out.println("XML方式的最终通知=================");
    }
}
