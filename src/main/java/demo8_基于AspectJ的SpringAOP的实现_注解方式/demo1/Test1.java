package demo8_基于AspectJ的SpringAOP的实现_注解方式.demo1;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test1 {
    @Test
    public void test(){
        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext6.xml");
        ProductDao productDao=(ProductDao)context.getBean("productDao");
        productDao.delete();
        productDao.findAll();
        productDao.findOne();
        productDao.save();
        productDao.update();
    }
}
