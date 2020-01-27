package demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * 切面类
 */

@Aspect
public class MyAspectJAnno {
  /**
   * 定义前置通知：通过value属性设置切点
   */
  @Before(value = "execution(* demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.ProductDao.save(..))")
  public void before(JoinPoint joinPoint){
      System.out.println("前置通知===================="+joinPoint);
  }

  @AfterReturning(value = "execution(* demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.ProductDao.update(..))")
  public void afterReturning(JoinPoint joinPoint){
      System.out.println("后置通知===================="+joinPoint);
  }
}
