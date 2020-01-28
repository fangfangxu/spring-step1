package demo9_基于AspectJ的SpringAOP的实现_xml方式;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlTest {

    @Test
    public void test(){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("applicationContext7.xml");
        CustomDao customDao= (CustomDao)applicationContext.getBean("customDao");
        customDao.save();
        customDao.delete();
        customDao.update();
        customDao.findOne();
        customDao.findAll();

    }
}
