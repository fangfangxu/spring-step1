# spring-home
第一章节：代理的学习历程

一、jdk动态代理

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

使用：

        UserDao userDao=new UserDaoImpl();
        MyJdkProxy myJdkProxy=new MyJdkProxy(userDao);
        UserDao proxy=(UserDao)myJdkProxy.createProxy();
        proxy.delete();
        proxy.find();
        proxy.save();
        proxy.update();

二、cglib动态代理

public class MyCglibProxy implements MethodInterceptor {

    private ProductDao productDao;

    public MyCglibProxy(ProductDao productDao) {
        this.productDao = productDao;
    }

    public Object createProxy() {
        //创建核心类
        Enhancer enhancer = new Enhancer();
        //设置父类
        enhancer.setSuperclass(productDao.getClass());
        //设置回调
        enhancer.setCallback(this);
        //生成代理
        Object proxy = enhancer.create();
        return proxy;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if ("save".equals(method.getName())) {
            System.out.println("****cglib动态代理save方法****");
            return methodProxy.invokeSuper(o, objects);
        }
        //代理类是目标类的子类
        //通过代理类去调用父类的方法
        return methodProxy.invokeSuper(o, objects);
    }
}

使用：

        ProductDao productDao=new ProductDao();
        MyCglibProxy myCglibProxy=new MyCglibProxy(productDao);
        ProductDao productProxy=(ProductDao)myCglibProxy.createProxy();
        productProxy.delete();
        productProxy.find();
        productProxy.update();
        productProxy.save();

三、传统Spring一般切面实现：对目标类的所有方法进行增强，使用通知作为切面

（1）接口+实现类

（2）自定义通知

（3）applicationContext.xml中配置bean，配置一般切面

    <!--配置目标类-->
    <bean id="studentDao"
          class="com.imooc.aop.demo3.StudentDaoImpl"></bean>
    <!--前置通知类型-->
    <bean id="myBeforeAdvice" class="com.imooc.aop.demo3.MyBeforeAdvice">
    </bean>
    <!--ProxyFactoryBean专门用来产生代理的-->
    <bean id="proxy" class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="target" ref="studentDao"></property>
    <property name="proxyInterfaces" value="com.imooc.aop.demo3.StudentDao"></property>
    <property name="interceptorNames" value="myBeforeAdvice"></property>
    </bean>

使用：

     ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        StudentDao studentDao = (StudentDao) context.getBean("proxy");
        studentDao.delete();
        studentDao.find();
        studentDao.update();
        studentDao.save();

四、传统Spring带有切点的切面实现：对目标类中某些方法进行增强，配置带有切入点的切面

    <!--配置目标类-->
    <bean id="customDao" class="com.imooc.aop.demo4.CustomDao"></bean>
    <!--配置通知-->
    <bean id="myAroundAdvice" class="com.imooc.aop.demo4.MyAroundAdvice"/>
    <!--配置带有切入点的切面-->
    <bean id="myAdvice" class="org.springframework.aop.support.RegexpMethodPointcutAdvisor">
        <!--<property name="pattern" value=".*save.*"/>-->
        <property name="patterns" value=".*save.*,.*delete.*"/>
        <property name="advice" ref="myAroundAdvice"/>
    </bean>
    <!--配置产生代理-->
    <bean id="customDaoProxy" class="org.springframework.aop.framework.ProxyFactoryBean">
        <property name="target" ref="customDao"/>
        <!--是否对类代理而不是接口-->
        <property name="proxyTargetClass" value="true"/>
        <property name="interceptorNames" value="myAdvice"/>
    </bean>

使用：

        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext2.xml");
        CustomDao customDao=(CustomDao)  context.getBean("customDaoProxy");
        customDao.delete();
        customDao.find();
        customDao.save();
        customDao.update();

五、Spring传统AOP无论是否是带有切入点的切面，产生一个代理类就需要配置一个ProxyFactoryBean,很麻烦。因此Spring
进而提供了自动创建代理。

（A）根据Bean名称自动创建代理：BeanNameAutoProxyCreator

    <!--配置目标类-->
    <bean id="testDao1" class="com.imooc.aop.demo5.TestDao1"/>
    <bean id="testDao2" class="com.imooc.aop.demo5.TestDao2"/>
    <!--配置通知-->
    <bean id="myAroundAdvice" class="com.imooc.aop.demo5.MyAroundAdvice"/>
    <!--配置BeanNameAutoProxyCreator完成自动代理的创建：切面为通知-->
    <bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
        <property name="beanNames" value="*Dao*"/>
        <property name="interceptorNames" value="myAroundAdvice"/>
    </bean>
使用：

        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext3.xml");
        TestDao1 testDao1=(TestDao1) context.getBean("testDao1");
        TestDao2 testDao2=(TestDao2) context.getBean("testDao2");
        testDao1.find();
        testDao1.save();
        testDao2.find();
        testDao2.save();

（B）基于切面自动创建代理：DefaultAdvisorAutoProxyCreator

    <!--配置目标类-->
    <bean id="customDao" class="com.imooc.aop.demo6.Custom"/>
    <bean id="userDao" class="com.imooc.aop.demo6.UserImpl"/>
    <!--配置通知-->
    <bean id="myAroundAdvice" class="com.imooc.aop.demo6.MyAroundAdvice"/>
    <!--配置切面-->
    <bean id="myAdvice" class="org.springframework.aop.support.RegexpMethodPointcutAdvisor">
        <property name="pattern" value="com\.imooc\.aop\.demo6\.Custom\.save" />
        <property name="advice" ref="myAroundAdvice"/>
    </bean>
    <!--基于切面的自动代理的创建-->
    <bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator">
    </bean>

使用：

        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext4.xml");
        Custom c2=(Custom) context.getBean("customDao");
        User u1=(User) context.getBean("userDao");
        c2.find();
        c2.save();
        u1.find();
        u1.save();

总结：

1、本章是对Spring传统的AOP的一个讲解，AOP即面向切面编程，是OOP面向对象编程的一个扩展和延伸，
是用来解决面向对象开发中的一些问题。

2、了解术语：连接点、切入点、织入、通知、目标对象、代理对象、切面。

3、Spring传统AOP底层实现原理：采用代理机制。Spring传统的AOP比较智能，会根据目标类是否实现接口
来自动进行切换是使用cglib动态代理还是jdk动态代理。

4、Spring传统AOP主要两大类：
   
   第一类：基于ProxyFactoryBean这种方式
  
   a.基于一般切面，通知即为切面，可以对目标类中的所有方法进行增强
               
   b.基于带有切入点的切面，可以对目标类中的某些方法进行增强
            
   第二类：自动代理
   
   a.基于Bean名称的自动代理
   
   b.基于切面信息产生代理
   
第二章节：前情回顾

1、IOC：控制反转，就是将原本程序中手动创建对象的控制权交给了
Spring容器去管理

2、DI:依赖注入：就是Spring创建对象的过程中，将这个对象依赖的属性注入进去

3、Spring工厂类有BeanFactory、ApplicationContext等等。二者有所不同。BeanFactory
是工厂实例化之后，在getBean时创建Bean。而ApplicationContext一加载配置文件时，就会将配置文件
中单例模式类全部实例化。

4、Spring三种实例化Bean方式：

（1）使用类构造器实例化

    <bean id=""  class=""/> 

（2）使用静态工厂方法实例化（简单工厂模式）

        Class:Bean
        Class:BeanFactory{
          public static Bean createBean(){
             return new Bean();
          }
        }
        <bean id="bean"  class="BeanFactory" factory-method="createBean"/> 

（3）实例工厂方法实例化

        Class:Bean
        Class:BeanFactory{
          public Bean createBean(){
             return new Bean();
          }
        }
        <bean id="beanFactory"  class="工厂类" />     
        <bean id="bean"  factory-bean="beanFactory"  factory-method="createBean"/> 
4、Spring创建的Bean的作用域：singleton、prototype（每次调用getBean（）时都会产生一个实例）、
request、session。

5、Spring容器中Bean的生命周期（11步）

参考：demo7_beanInital

步骤一：执行构造方法

步骤二：设置属性

步骤三：实现BeanNameAware，执行setBeanName

步骤四：实现BeanFactoryAware，执行setBeanFactory

步骤五：存在类实现BeanPostProcessor，执行前处理方法

步骤六：实现InitializingBean，执行afterPropertiesSet

步骤七：执行init-method中指定的myinit

步骤八：存在类实现BeanPostProcessor，执行后处理方法

步骤九：自身业务方法

步骤十：实现DisposableBean，执行destroy

步骤十一：执行destroy-method中指定的mydestroy

6、Spring的Bean管理：注解方式

（1）applicationContext.xml中开启注解扫描：
          
     <context:component-scan base-package="要扫描的包" />
（2）类上使用注解：
      
      @Repository、@Service、@Controller   

7、spring配置里<context:annotation-config>和<context:component-scan>区别
 
    <context:annotation-config/>
    annotation-config处理@autowired之类的注解（共有四类） 前提是注解作用的类已经被注册到spring容器里（bean id=“” class=“”） 
    <context:component-scan  base-package="包名" />
    component-scan除了包含annotation-config的作用外，还能自动扫描和注册base-package下有@component之类注解的类，将其作为bean注册到spring容器里        