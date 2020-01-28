package demo10_jdbctemplate;

import javafx.application.Application;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTest1 {
  private JdbcTemplate jdbcTemplate;

    {
        ApplicationContext context=new ClassPathXmlApplicationContext("spring.xml");
         jdbcTemplate=(JdbcTemplate)context.getBean("jdbcTemplate");
    }

    @Test
    public void testExecute(){

        jdbcTemplate.execute("create table user2(id int,name varchar (20))");
    }
}
