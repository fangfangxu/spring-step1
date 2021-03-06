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

5、SpringAop基于AspectJ的实现：AspectJ是一个基于java语言的AOP框架，Spring传统的AOP在
有了AspectJ框架后，对该框架进行了集成，简化了SpringAOP的实现，即新版本的SpringAOP均是
基于AspectJ实现的。（1、注解方式  2、xml方式）

一、开发环境的搭建：基于AspectJ

1、
（1）pom文件中引入spring相关依赖、springaop依赖、aspectj依赖

（2）applicationContext.xml中配置约束
          
        <?xml version="1.0" encoding="UTF-8"?>
       <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop" xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
             http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--开启AspectJ的注解开发，自动代理=====================-->
    <aop:aspectj-autoproxy/>
    </beans>
   
2、@AspectJ提供不同的通知类型：
  
    @Before前置通知，相当于BeforeAdvice 
    @AfterReturning前置通知，相当于AfterReturningAdvice
    @Around环绕通知相当于MethodInterceptor
    @AfterThrowing异常抛出通知，相当于ThrowAdvice
    @After 最终final通知，不管是否异常，该通知都会执行
    @DeclareParents引介通知，相当于IntroductionInterceptor(不要求掌握)   

3、在通知中通过value属性定义切点

    通过execution函数，可以定义切点的方法切入
    语法：
    -execution（<访问修饰符>？<返回类型><方法名>（<参数>）<异常>）
    访问修饰符可以省略
    例如：
    匹配所有类public方法 execution（public * *（..））
    匹配指定包下所有类方法 execution（* com.imooc.dao.*（..））不包含子包
    execution（* com.imooc.dao..*（..））包含子包
    匹配指定类所有方法
    execution（* com.imooc.service.UserService.*（..））
    匹配实现特定接口所有类方法
    execution（* com.imooc.dao.GenericDAO+.*（..））
    +号代表子类
    匹配所有save开头的方法
    execution（* save*(..））

4、案例：demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1

(1)定义目标类：
      
    public class ProductDao {
    public void save(){
        System.out.println("保存商品...");
    }

    public void delete(){
        System.out.println("删除商品...");
    }

    public String update(){
        System.out.println("修改商品...");
        return "hello";
    }
    public void findOne(){
        System.out.println("查询一个商品...");
     //        int i=1/0;
    }

    public void findAll(){
        System.out.println("查询所有商品...");
     //        int i=1/0;
    }
     }

（2）定义切面：

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

（3）applicationContext.xml

     <?xml version="1.0" encoding="UTF-8"?>
     <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:aop="http://www.springframework.org/schema/aop" xsi:schemaLocation="
             http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                  http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
         <!--开启AspectJ的注解开发，自动代理=====================-->
         <aop:aspectj-autoproxy/>
         <!--配置目标类-->
         <bean id="productDao" class="demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.ProductDao"/>
         <!--定义切面-->
         <bean id="myAspectJAnno" class="demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1.MyAspectJAnno"/>
     </beans>
 
5、案例：demo9_基于AspectJ的SpringAOP的实现_xml方式

（1）定义接口+实现类

（2）定义切面

    /**
     * 切面类
     */
    public class MyAspectXml {
       //前置通知
        public void before(JoinPoint joinPoint){
            System.out.println("XML方式的前置通知================="+joinPoint);
        }
    
        //后置通知
        public void afterReturning(Object result){
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

（3）applicationContext.xml

     <?xml version="1.0" encoding="UTF-8"?>
     <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:aop="http://www.springframework.org/schema/aop" xsi:schemaLocation="
             http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                  http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
         <!--Xml配置方式完成AOP的开发=====================-->
         <!--配置目标类-->
        <bean id="customDao" class="demo9_基于AspectJ的SpringAOP的实现_xml方式.CustomDaoImpl" />
         <!--配置切面类-->
        <bean id="myAspectXml" class="demo9_基于AspectJ的SpringAOP的实现_xml方式.MyAspectXml"/>
         <!--AOP的相关配置-->
        <aop:config>
            <aop:pointcut id="pointcut1" expression="execution(* demo9_基于AspectJ的SpringAOP的实现_xml方式.CustomDao.save(..))"/>
            <aop:pointcut id="pointcut2" expression="execution(* demo9_基于AspectJ的SpringAOP的实现_xml方式.CustomDao.delete(..))"/>
            <aop:pointcut id="pointcut3" expression="execution(* demo9_基于AspectJ的SpringAOP的实现_xml方式.CustomDao.update(..))"/>
            <aop:pointcut id="pointcut4" expression="execution(* demo9_基于AspectJ的SpringAOP的实现_xml方式.CustomDao.findOne(..))"/>
            <aop:pointcut id="pointcut5" expression="execution(* demo9_基于AspectJ的SpringAOP的实现_xml方式.CustomDao.findAll(..))"/>
     
            <aop:aspect ref="myAspectXml">
                    <aop:before method="before" pointcut-ref="pointcut1"/>
                    <aop:after-returning method="afterReturning" pointcut-ref="pointcut2" returning="result"/>
                    <aop:around method="around" pointcut-ref="pointcut3"/>
                    <aop:after-throwing method="throwing" pointcut-ref="pointcut4" throwing="e"/>
                    <aop:after method="after" pointcut-ref="pointcut5"/>
                </aop:aspect>
        </aop:config>
     
     </beans>
      
        
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
    

第三章节：Spring组件：JDBC Template 简化持久化操作

1、环境搭建

（1）Maven
    -Mysql驱动  -Spring组件（beans、core、context、aop）
    
（2）Spring配置：spring.xml（配置两个Bean）
     -数据源   -JDBC Template    
     
        <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
             <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
             <property name="url" value="jdbc:mysql://localhost:3306/selection_course?useUnicode=true&amp;charactorEncoding=utf-8"/>
             <property name="username" value="root"/>
             <property name="password" value="123456"/>
         </bean>
     
         <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
                <property name="dataSource" ref="dataSource"/>
         </bean>
 
（3）jdbcTemplate工具类的简单使用

（1）execute方法：通常DDL语句

（2）update方法：单条新增、修改

（3）batchUpdate方法：多条新增、修改
 
（4）queryForObject方法：查询单一结果值

（5）queryForList方法：查询List<单一结果值>

（6）queryForMap方法：查询复杂对象封装为Map：单条

（7）queryForList方法：查询复杂对象封装为Map：多条

（8）queryForObject方法：查询复杂对象封装为实体对象：单条

（9）query方法：查询复杂对象封装为实体对象：多条

    public class JdbcTest1 {
       private JdbcTemplate jdbcTemplate;
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
    }

    /*********************************************************
     * （1）execute方法：通常DDL语句
     * 一、【execute(final String sql)】
     *********************************************************/
    public void testExecute() {
        jdbcTemplate.execute("create table user2(id int,name varchar (20))");
    }

    /*********************************************************
     * 二.增删改
     ********************************************************/

    /**
     * （2）update方法：单条新增、修改
     * 【 update(String sql, Object... args)】
     */
    public void update1() {
        String sql = "insert into student (name,sex) values (?,?)";
        jdbcTemplate.update(sql, new Object[]{"张飞", "男"});
    }

    public void update2() {
        String sql = "update student set name=? where id=?";
        jdbcTemplate.update(sql, "李四", 1);
    }

    /**
     * （3）batchUpdate方法：多条新增、修改
     * 【int[] batchUpdate(final String... sql)】
     */
    public void batchUpdate() {
        String[] sqls = {
                "insert into student(name,sex) values ('关羽','女')",
                "insert into student(name,sex) values ('张飞','男')",
                "update student set sex='女' where id ='3'"
        };
        jdbcTemplate.batchUpdate(sqls);
    }

    /**
     * 【int[] batchUpdate(String sql, List<Object[]> batchArgs) 】多条新增、修改【同构】
     */
    public void batchUpdate2() {
        String sql = "insert into selection(student,course) values(?,?)";
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{3, 1001});
        list.add(new Object[]{3, 1003});
        jdbcTemplate.batchUpdate(sql, list);
    }

    /*********************************************************
     * 三.查询
     *********************************************************/

    /**
     * （4）queryForObject方法：查询单一结果值
     * 【 T queryForObject(String sql, Class<T> requiredType)】
     */
    public void queryOne() {
        String sql = "select count(*) from student";
        int i = jdbcTemplate.queryForObject(sql, Integer.class);
        System.out.println(i);
    }

    /**
     * （5）queryForList方法：查询List<单一结果值>
     * 【 List<T> queryForList(String sql, Class<T> elementType, Object... args)】
     */
    public void queryList() {
        String sql = "select name from student where sex=?";
        List<String> names = jdbcTemplate.queryForList(sql, String.class, "女");
        for (String name : names) {
            System.out.print(" " + name);
        }
    }

    /**
     * （6）queryForMap方法：查询复杂对象封装为Map：单条
     * 【 Map<String, Object> queryForMap(String sql, Object... args)】
     */
    public void queryForMap1() {
        String sql = "select * from student where id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, 1);
        System.out.println(map);
    }

    /**
     * （7）queryForList方法：查询复杂对象封装为Map：多条
     * 【 List<Map<String, Object>> queryForList(String sql)】
     */
    public void queryForMap2() {
        String sql = "select * from student";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        System.out.println(list);
    }

    /**
     * 查询复杂对象-->封装为实体对象：通过RowMapper接口
     * RowMapper：将实体类的各个属性与数据库表的各个字段进行一一映射的
     * queryForObject query
     */


    /**
     * （8）queryForObject方法：查询复杂对象封装为实体对象：单条
     * 【T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) 】
     */
    @Test
    public void queryTest1() {
        String sql = "select * from student where id=?";
        Student stu = jdbcTemplate.queryForObject(sql, new Object[]{1}, new StudentRowMapper());
        System.out.println(stu);
    }

    /**
     * （9）query方法：查询复杂对象封装为实体对象：多条
     * 【List<T> query(String sql, RowMapper<T> rowMapper)】
     */
    @Test
    public void queryTest2() {
        String sql = "select * from student";
        List<Student> stu = jdbcTemplate.query(sql, new StudentRowMapper());
        System.out.println(stu);
    }

    private class StudentRowMapper implements RowMapper<Student> {
        @Override
        public Student mapRow(ResultSet resultSet, int i) throws SQLException {
            Student stu = new Student();
            stu.setId(resultSet.getInt("id"));
            stu.setName(resultSet.getString("name"));
            stu.setSex(resultSet.getString("sex"));
            stu.setBorn(resultSet.getDate("born"));
            return stu;
        }
    }}

2、Jdbc Template

（1）优点：简单、灵活

（2）缺点：SQL与java代码掺杂在一起；功能不丰富（没分页、没完善的对象与对象之间对应关系）
          
（3）它想将JDBC API往ORM上引，但是只迈出了一小步

（4）它是Spring框架对JDBC操作的封装，简单、灵活但不够强大。还得结合其他优秀的
ORM框架来对持久层进行操作：eg：Mybatis


第四章节：Spring事务管理

事务：

1、概念：事务一般特指数据库事务，是指作为一个程序执行单元执行的一系列操作，要么完全执行，要么完全不执行

2、事务的特性：

①原子性（atomicity）：一个事务是一个不可分割的工作单位
（结构上保证我的事务只有两种状态）

②一致性（consistency）：事务必须是使数据库从一个一致性状态变到另一个一致性状态
（从业务上保证事务是合理的）

③隔离性（isolation）：一个事务的执行不能被其他事务干扰
（并发时，两个事务之间不能相互干扰）

④持久性（durability）：一个事务一旦提交，他对数据库中数据的改变应该是永久性的

3、MySQL事务处理

（1）Mysql处理事务基本语句

      基本规则
          只有InnoDB这种引擎的数据库才支持事务
      语句：
           MySQL默认以自动提交的模式运行；
           MySQL事务处理语句：
           BEGIN或STARTTRANSACTION显式地开启事务；
           COMMIT提交事务，并使已对数据库进行的所有修改变为永久性的
           ROLLBACK回滚事务，并撤销正在进行的所有未提交的修改   
      eg：
           -- 查看数据库引擎
           show ENGINES;
           -- 手动开启事务
           begin;
           select * from products;
           update products set stock=90 where id='100001'
           select * from products;
           rollback;
           select * from products;

（2）Mysql事务处理---并发问题
               
     事务并发问题：
     1、脏读：读取了脏数据，即读取了不合理的、错误的或者本来不存在的数据；
     (脏读:事务A、B，在事务B写SQL语句更新操作，事务A执行查询操作，但B没有提交事务，而是回滚事务，事务A读取到假数据的过程)
     解决方法：通过限制只能读取永久性的数据
     2、不可重复读：在事务执行的过程中重复读取的同一项数据，但是读取到的结果是不一样的
     （由于事务B修改对事务A造成了影响：事务A读取A商品
     库存为100，B事务修改A商品库存为0并commit，A事务再次读取A商品库存为0，两次读取结果不一样）
     解决办法：通过锁行解决
     3、幻读：由于事务B插入对事务A造成了影响
     （如事务A将所有商品的库存量改为0，交叉执行的事务B又插入了库存为100的新商品，
     此时事务A再次查询修改结果发现仍有一个库存量为100的商品）
     解决办法：通过锁表解决        
     
（3）Mysql事务处理---事务隔离级别    

     Mysql事务隔离级别（从上到下执行效率越来越低）：
     1、读未提交（read-uncommitted）：没有解决脏读、不可重复读和幻读的问题
     2、读已提交（read-committed）：只解决了脏读的问题
     3、可重复读（repeatable-read）：解决的脏读和不可重复读的问题
     4、串行化（serializable）：解决了脏读、不可重复读和幻读的问题
     执行语句：
     select @@tx_isolation（8.0以后版本的查询语句为select @@transaction_isolation）查询默认隔离级别；
     set session transaction isolation level XXX 设置当前会话隔离级别(xxx为名称 eg read-uncommitted)
     
     番外：
     一个session 连续开启两个事务，第一事务不手动提交，也不手动回滚，然后立即开启第二个事务。
     那第一个事务到底是回滚还是提交？第一个事务在开启第二个begin之后提交了，其实和alter table一个原理，
     在这里第二个begin触发了MySQL的隐式提交

    -- 每个窗口都得重新设置会话级别：事务隔离级别-读未提交：不能解决脏读、不可重复读、幻读
    select @@transaction_isolation ;
    set session transaction isolation level read uncommitted ;
    
    
    -- 【脏读】
    -- session1
    -- 事务A
    -- (3)
    begin;
    select * from products where id='100001';
    rollback;
    select * from products where id='100001' for update;
    
    select @@transaction_isolation ;
    set session transaction isolation level read uncommitted ;
    
    -- session2
    -- 事务B
    -- (1)
    begin;
    -- (2)
    select * from products where id='100001';
    update products set stock=0  where id='100001';
    select * from products where id='100001';
    -- (4)
    rollback;
    
    -- 【不可重复读】
    -- 事务A
    BEGIN;
    -- (1)
    select * from products where id='100001';
    -- (3)
    select * from products where id='100001';
    rollback;
    
    -- 事务B
    -- (2)
    begin;
    update products set stock=50 where id='100001';
    commit;

    -- 【幻读】
    -- 事务A
    begin;
    -- (1)
    select * from products;
    update products set stock=0;
    -- (3)
    select * from products;
    commit;
    
    -- 事务B
    -- (2)
    begin;
    select * from products;
    insert into products(id,title,price,stock,status) VALUES(1,'小米test',100,100,'正常');
    select * from products;
    commit;
    
4、 JDBC事务处理
   
   （1）JDBC处理事务基本语句   
    
    Connection接口是JDBC进行事务处理的核心，必须是基于一个Connection对象的多个sql操作才能封装成一个事务，不同的Connection对象没办法封装成一个事务；
          JDBC默认事务处理行为是自动提交；
    事务相关方法：
         setAutoCommit设置自动提交；
         rollback回滚事务；
         commit提交事务；
         
    public class JdbcTest {
        private String driver = "com.mysql.jdbc.Driver";
        private String url = "jdbc:mysql://localhost:3306/os?useUnicode=true&charactorEncoding=utf-8&serverTimezone=UTC";
        private String username = "root";
        private String password = "123456";
    
        @Test
        public void addOrder() {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(url, username, password);
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();
                statement.execute("insert into orders values('100001','100001',2,2499,now(),null,null,'刘备','13000000000','成都','待发货')");
                statement.execute("update products set stock=stock-2 where id='100001'");
                statement.close();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }     
 
（2）JDBC事务隔离级别：用来解决事务并发产生的问题

     JDBC事务隔离级别
     隔离级别
     -TRANSACTION_NONE(不支持事务) 0;
     -TRANSACTION_READ_UNCOMMITTED(读未提交）1；
     -TRANSACTION_READ_COMMITTED（读已提交）2；
     -TRANSACTION_REPEATABLE_READ（可重复读）4；
     _TRANSACTION_SERIALIZABLE （串行化）8；
     事务隔离级别设置
     -getTransactionIsolation 获取当前隔离级别
     -setTransactionIsolation 设置隔离级别 默认为4
 
 
 5、 Spring事务处理   
 
 1、基本概念
 
    （1）
       PlatformTransactionManager 事务管理器
       TransactionDefinition 事务定义
       TransactionSatus 事务状态/正在运行的事务
    事务定义（TransactionDefinition）传给事务管理器（PlatformTransactionManager） 
    事务管理器创建出一个正在运行的事务（TransactionSatus）
    
    （2）依次介绍以上接口中的方法
    1、TransationDenfinition接口：
    隔离级别：
        ISOLATION_DEFAULT使用数据库默认；
        ISOLATION_READ_UNCOMMITTED；
        ISOLATION_READ_COMMITTED；
        ISOLATION_REPEATABLE_READ；
        ISOLATION_SERIALIZABLE；
    默认超时：
        TIMEOUT_DEFAULT默认30秒；
    事务传播行为(方法调用方法时，被调用方法的一个事务传递的问题)：
         PROPAGTION_REQUIRED支持当前事务，如果当前没有事务，就新建一个事务（最常用，Spring默认）
         eg：针对b(),b方法必须以事务的方式执行，现在b是被a调用，那么两种情况：a本身是否有事务？
         情况一：a没事务，b就会封装成一个事务；
         
         public class Propagtion {       
             public void a(){
                 //步骤
                  b();
             }                 
             public void b(){
                 //begin
                 //步骤
                 //commit
             }
         }
         情况二：a有事务，b会加入到a的事务里
         
         public class Propagtion {
         
             public void a(){
                 //begin
                 //步骤
                  b();
                 //commit
             }
             public void b(){
             //步骤
             }
         }
         
         以上保证b方法里的这些步骤的执行一定是在一个事务里
         
2、Spring事务处理实现   

(1)持久层使用Jdbc Template

(2)Spring实现事务两种方式：

   a.编程式事务处理(基于底层API、基于TransactionTemplate 两种方式)
   
   b.声明式事务处理  
   
 【 a.编程式事务处理 】
  
  基于底层API:事务定义、事务管理器、事务状态
  
 （1）Spring基于底层API的事务实现
 
        spring-dao.xml:
        
            <context:component-scan base-package="demo11_tx.dao"/>
            <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
                <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
                <property name="url"
                          value="jdbc:mysql://localhost:3306/os?useUnicode=true&amp;charactorEncoding=utf-8&amp;serverTimezone=UTC"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </bean>
        
            <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
                <property name="dataSource" ref="dataSource"/>
            </bean>
      
      
        spring-service1.xml:
        
         <import resource="spring-dao.xml"/>
            <context:component-scan base-package="demo11_tx.service.impl1"/>
           <!--定义事务管理器：事务管理器实现类的选择：取决于持久层使用什么实现
           Spring Jdbc Template选择DataSourceTransactionManager事务管理器，
           那么DataSourceTransactionManager事务管理器，至少要告诉事务管理器你使用的数据源
           是哪个-->
            <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
                <property name="dataSource" ref="dataSource"/>
            </bean>
           <!--事务定义:事务传播行为、事务隔离级别、事务传播时间-->
            <bean id="transactionDefination" class="org.springframework.transaction.support.DefaultTransactionDefinition">
            <property name="propagationBehaviorName"  value="PROPAGATION_REQUIRED"/>
            </bean>
        
        Service实现：
        
        @Service
        public class OrderServiceImpl implements OrderService {
            @Autowired
            private OrderDao orderDao;
            @Autowired
            private ProductDao productDao;
            @Autowired
            @Qualifier("transactionManager")
            private PlatformTransactionManager platformTransactionManager;
            @Autowired
            @Qualifier("transactionDefination")
            private TransactionDefinition transactionDefinition;
        
            @Override
            public void addOrder(Order order) {
                //开启事务/获取事务
                TransactionStatus ts = platformTransactionManager.getTransaction(transactionDefinition);
                try {
                    //第一步 生成订单
                    orderDao.insert(order);
                    //第二步 修改库存
                    Product product = productDao.select(order.getProductsId());
                    product.setStock(product.getStock() - order.getNumber());
                    productDao.update(product);
                    //提交事务
                    platformTransactionManager.commit(ts);
                } catch (Exception e) {
                    //事务回滚
                    e.printStackTrace();
                    platformTransactionManager.rollback(ts);
                }
            }
        }
        
 
 （2）Spring基于基于TransactionTemplate的事务实现(简化基于底层API方式繁琐的步骤)        
        
        spring-dao.xml:
        
            <context:component-scan base-package="demo11_tx.dao"/>
            <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
                <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
                <property name="url"
                          value="jdbc:mysql://localhost:3306/os?useUnicode=true&amp;charactorEncoding=utf-8&amp;serverTimezone=UTC"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </bean>
        
            <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
                <property name="dataSource" ref="dataSource"/>
            </bean>
      
      
        spring-service2.xml:
        
            <import resource="spring-dao.xml"/>
            <context:component-scan base-package="demo11_tx.service.impl2"/>
            <!--定义事务管理器：事务管理器实现类的选择：取决于持久层使用什么实现
          Spring Jdbc Template选择DataSourceTransactionManager事务管理器，
          那么DataSourceTransactionManager事务管理器，至少要告诉事务管理器你使用的数据源
          是哪个-->
            <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
                <property name="dataSource" ref="dataSource"/>
            </bean>
           <!--transaction模板-->
            <bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
                <property name="transactionManager" ref="transactionManager"/>
            </bean>
        
    Service实现：
    
    @Service
    public class OrderServiceImpl implements OrderService {
        @Autowired
        private OrderDao orderDao;
        @Autowired
        private ProductDao productDao;
        @Autowired
        private TransactionTemplate transactionTemplate;
    
        @Override
        public void addOrder(final Order order) {
            transactionTemplate.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus transactionStatus) {
                    try {
                        //第一步 生成订单
                        orderDao.insert(order);
                        //第二步 修改库存
                        Product product = productDao.select(order.getProductsId());
                        product.setStock(product.getStock() - order.getNumber());
                        productDao.update(product);
                    } catch (Exception e) {
                        //事务回滚
                        e.printStackTrace();
                        transactionStatus.setRollbackOnly();
                    }
                    return null;
                }
            });
        }
    }   

 【 b.声明式事务处理 】   

 -Spring的声明事务处理是建立在AOP的基础之上的。其本质是对方法前后进行拦截，然后再目标方法
  之前：创建或者加入一个事务，在执行完目标方法之后：根据执行情况提交或回滚事务
  
 -建议在开发中使用声明式事务，是因为这样可以使得业务代码纯粹干净，方便后期的代码维护。 
 
 （1）基于TransactionInterceptor的声明式事务处理
 
 （2）基于TransactionProxyFactoryBean的声明式事务处理
 
 （3）基于<tx>命名空间的声明式事务处理
 
 （4）基于@Transactional的声明式事务处理
 

    （1）基于TransactionInterceptor的声明式事务处理
 
 
         spring-dao.xml:
         
             <context:component-scan base-package="demo11_tx.dao"/>
             <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
                 <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
                 <property name="url"
                           value="jdbc:mysql://localhost:3306/os?useUnicode=true&amp;charactorEncoding=utf-8&amp;serverTimezone=UTC"/>
                 <property name="username" value="root"/>
                 <property name="password" value="123456"/>
             </bean>
         
             <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
                 <property name="dataSource" ref="dataSource"/>
             </bean>
       
       
         spring-service3.xml:
 
             <import resource="spring-dao.xml"/>
             <!--<context:component-scan base-package="demo11_tx.service.impl"/>-->
             <!--定义事务管理器：事务管理器实现类的选择：取决于持久层使用什么实现
           Spring Jdbc Template选择DataSourceTransactionManager事务管理器，
           那么DataSourceTransactionManager事务管理器，至少要告诉事务管理器你使用的数据源
           是哪个-->
             <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
                 <property name="dataSource" ref="dataSource"/>
             </bean>
         
             <!--(1)配置要拦截的目标类-->
             <bean id="orderServiceTarget" class="demo11_tx.service.impl.OrderServiceImpl"/>
         
             <!--(2)配置拦截器-->
             <bean id="transactionInterceptor" class="org.springframework.transaction.interceptor.TransactionInterceptor">
                 <!--事务管理器-->
                 <property name="transactionManager" ref="transactionManager"/>
                 <!--定义事务传播行为、事务隔离级别、只读等等-->
                 <property name="transactionAttributes">
                     <props>
                         <!--拦截方法：应用事务-->
                         <prop key="get*">PROPAGATION_REQUIRED,readOnly</prop>
                         <prop key="find*">PROPAGATION_REQUIRED,readOnly</prop>
                         <prop key="query*">PROPAGATION_REQUIRED,readOnly</prop>
                         <prop key="*">PROPAGATION_REQUIRED</prop>
                     </props>
                 </property>
             </bean>
         
             <!--(3)将目标对象和拦截器进行关联:用拦截器去增强目标对象，增强后的对象才是我们要用的对象-->
               <bean id="orderService" class="org.springframework.aop.framework.ProxyFactoryBean">
                     <property name="target" ref="orderServiceTarget"/>
                      <property name="interceptorNames">
                              <list>
                                  <idref bean="transactionInterceptor"/>
                              </list>
                      </property>
               </bean>        
               
               
               Service：
               /**
                * 声明式事务管理：基于AOP
                */
               @Service
               public class OrderServiceImpl implements OrderService {
                   @Autowired
                   private OrderDao orderDao;
                   @Autowired
                   private ProductDao productDao;
               
                   @Override
                   public void addOrder(Order order) {
                           //第一步 生成订单
                           orderDao.insert(order);
                           //第二步 修改库存
                           Product product = productDao.select(order.getProductsId());
                           product.setStock(product.getStock() - order.getNumber());
                           productDao.update(product);
                           //提交事务
                   }
               }
               
 （2）基于TransactionProxyFactoryBean的声明式事务处理：简化拦截器配置           

        spring-service4.xml:     
              
              <import resource="spring-dao.xml"/>
               <!--<context:component-scan base-package="demo11_tx.service.impl"/>-->
               <!--定义事务管理器：事务管理器实现类的选择：取决于持久层使用什么实现
             Spring Jdbc Template选择DataSourceTransactionManager事务管理器，
             那么DataSourceTransactionManager事务管理器，至少要告诉事务管理器你使用的数据源
             是哪个-->
               <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
                   <property name="dataSource" ref="dataSource"/>
               </bean>
           
               <!--(1)配置要拦截的目标类-->
               <bean id="orderServiceTarget" class="demo11_tx.service.impl.OrderServiceImpl"/>
           
               <!--(2)配置TransactionProxyFactoryBean=TransactionInterceptor+增强类（ProxyFactoryBean产生）-->
               <bean id="orderService" class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
                   <!--事务管理器-->
                   <property name="transactionManager" ref="transactionManager"/>
                   <!--定义事务传播行为、事务隔离级别、只读等等-->
                   <property name="transactionAttributes">
                       <props>
                           <!--拦截方法：应用事务-->
                           <prop key="get*">PROPAGATION_REQUIRED,readOnly</prop>
                           <prop key="find*">PROPAGATION_REQUIRED,readOnly</prop>
                           <prop key="query*">PROPAGATION_REQUIRED,readOnly</prop>
                           <prop key="*">PROPAGATION_REQUIRED</prop>
                       </props>
                   </property>
                   <!--配置目标类-->
                   <property name="target" ref="orderServiceTarget"/>
               </bean>    
               
 
 （3）基于<tx>命名空间的声明式事务处理【重点】
 
              <import resource="spring-dao.xml"/>
              <context:component-scan base-package="demo11_tx.service.impl"/>
              <!--定义事务管理器：事务管理器实现类的选择：取决于持久层使用什么实现
            Spring Jdbc Template选择DataSourceTransactionManager事务管理器，
            那么DataSourceTransactionManager事务管理器，至少要告诉事务管理器你使用的数据源
            是哪个-->
              <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
                  <property name="dataSource" ref="dataSource"/>
              </bean>
          
              <!--定义事务通知-->
              <tx:advice id="txAdvice" transaction-manager="transactionManager">
              <tx:attributes>
                     <tx:method name="*" propagation="REQUIRED" read-only="false"/>
              </tx:attributes>
              </tx:advice>
          
              <!--定义aop：哪些类那些方法应用了事务通知的增强-->
              <aop:config>
                   <aop:pointcut id="pointcut" expression="execution(* demo11_tx.service.impl.*.*(..))"/>
                   <aop:advisor advice-ref="txAdvice" pointcut-ref="pointcut"/>
              </aop:config>      
              
（4）基于@Transactional的声明式事务处理

     spring-service6.xml:
       <import resource="spring-dao.xml"/>
       <context:component-scan base-package="demo11_tx.service.impl6"/>
       <!--定义事务管理器：事务管理器实现类的选择：取决于持久层使用什么实现
     Spring Jdbc Template选择DataSourceTransactionManager事务管理器，
     那么DataSourceTransactionManager事务管理器，至少要告诉事务管理器你使用的数据源
     是哪个-->
       <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
           <property name="dataSource" ref="dataSource"/>
       </bean>
   
       <!--使用tx注解驱动指定关于事务的注解，并指定事务管理器transactionManager-->
       <tx:annotation-driven transaction-manager="transactionManager"/>
    </beans>    
    
    service:
        @Transactional(propagation=Propagation.REQUIRED)
        @Override
        public void addOrder(Order order) {
                //第一步 生成订单
                orderDao.insert(order);
                //第二步 修改库存
                Product product = productDao.select(order.getProductsId());
                product.setStock(product.getStock() - order.getNumber());
                productDao.update(product);
                //提交事务
        }
        
   事务属性范围包括传播行为，隔离规则，回滚规则，事务超时，是否只读，并没有是否提交。
   
   Spring提供了7种事务传播行为，当一个事务方法被另一个事务方法调用时，必须指定事务应如何传播。   
   
   JDBC有自动模式和手动模式两种情况，JDBC事务控制的局限性在一个数据库连接内，不可以跨越多个数据库。
        
   Spring的开发规范存在于docs目录下。
   
   SpringIOC是通过工厂模式+反射模式+配置文件共同实现组件间的解耦的。
   
   依赖注入是在IOC的基础上完成的；并不是IOC的完成依赖于依赖注入。    
   
      补充：
      
      1、AOP（Aspect Orient Programming），作为面向对象编程的一种补充。
      2、AOP 代理则可分为静态代理和动态代理两大类。
      3、静态代理：AspectJ，AspectJ是编译时增强
      4、动态代理：Spring AOP
      说明（曾经以为AspectJ是Spring AOP一部分，是因为Spring AOP使用了AspectJ的Annotation。
      使用了Aspect来定义切面,使用Pointcut来定义切入点，使用Advice来定义增强处理。虽然使用了Aspect
      的Annotation，但是并没有使用它的编译器和织入器。其实现原理是JDK 动态代理，在运行时生成代理类。
      为了启用 Spring 对 @AspectJ 方面配置的支持，并保证 Spring 容器中的目标 Bean 被一个或多个方面
      自动增强，必须在 Spring 配置文件中添加如下配置
         <aop:aspectj-autoproxy/>
      当启动了 @AspectJ 支持后，在 Spring 容器中配置一个带 @Aspect 注释的 Bean，Spring 将会自动识别该
       Bean，并将该 Bean 作为方面 Bean 处理。方面Bean与普通 Bean 没有任何区别，一样使用 <bean.../> 
       元素进行配置，一样支持使用依赖注入来配置属性值。）         