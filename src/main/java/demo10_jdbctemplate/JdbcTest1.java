package demo10_jdbctemplate;

import javafx.application.Application;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JdbcTest1 {
    private JdbcTemplate jdbcTemplate;

    {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
    }

    /**
     * 一、通常DDL语句
     */

    public void testExecute() {
        jdbcTemplate.execute("create table user2(id int,name varchar (20))");
    }

    /**
     * 二.1、更新、新增
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
     * 二.2、批量更新
     */
    public void batchUpdate() {
        String[] sqls = {
                "insert into student(name,sex) values ('关羽','女')",
                "insert into student(name,sex) values ('张飞','男')",
                "update student set sex='女' where id ='3'"
        };
        jdbcTemplate.batchUpdate(sqls);
    }

    public void batchUpdate2() {
        String sql = "insert into selection(student,course) values(?,?)";
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{3, 1001});
        list.add(new Object[]{3, 1003});
        jdbcTemplate.batchUpdate(sql, list);
    }

    /**
     * 三.查询
     */

    public void queryOne() {
        String sql = "select count(*) from student";
        int i = jdbcTemplate.queryForObject(sql, Integer.class);
        System.out.println(i);
    }

    public void queryList() {
        String sql = "select name from student where sex=?";
        List<String> names = jdbcTemplate.queryForList(sql, String.class, "女");
        for (String name : names) {
            System.out.print(" " + name);
        }
    }

    public void queryForMap1(){
        String sql="select * from student where id=?";
        Map<String, Object> map=jdbcTemplate.queryForMap(sql,1);
//        Set<Map.Entry<String, Object>> entyrs=map.entrySet();
//        for(Map.Entry<String, Object> entry:entyrs){
//            System.out.println("key:"+entry.getKey()+",value:"+entry.getValue());
//        }
        System.out.println(map);
    }
    @Test
    public void queryForMap2(){
        String sql="select * from student";
        List<Map<String, Object>> list=jdbcTemplate.queryForList(sql);
        System.out.println(list);
    }

}
