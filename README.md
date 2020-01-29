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