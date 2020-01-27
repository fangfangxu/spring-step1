package demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * 切面类
 */

@Aspect
public class MyAspectJAnno {
  /**
   * 定义前置通知：通过value属性设置切点
   */
  @Before(value = "myPointcut1()")
  public void before(JoinPoint joinPoint){
      System.out.println("前置通知======================"+joinPoint);
  }

  @AfterReturning(value = "myPointcut2()",returning = "result")
  public void afterReturning(Object result){
      System.out.println("后置通知======================"+result);
  }
 @Around(value="myPointcut3()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
      System.out.println("环绕前通知======================");
      Object object=joinPoint.proceed();
      System.out.println("环绕后通知======================");
      return object;
  }

  @AfterThrowing(value = "myPointcut4()",throwing = "e")
  public void afterThrowing(Throwable e){
      System.out.println("异常抛出通知======================"+e.getMessage());
  }

  @After(value = "myPointcut5() || myPointcut1()")
  public void after(){
      System.out.println("最终通知======================");
  }

  @Pointcut(value = "execution(* demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.ProductDao.save(..))")
  private void  myPointcut1(){};

    @Pointcut(value = "execution(* demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.ProductDao.delete(..))")
    private void  myPointcut2(){};


    @Pointcut(value = " execution(* demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.ProductDao.update(..))")
    private void  myPointcut3(){};


    @Pointcut(value = " execution(* demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.ProductDao.findOne(..))")
    private void  myPointcut4(){};


    @Pointcut(value = "execution(* demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.ProductDao.findAll(..))")
    private void  myPointcut5(){};

}
