package demo10_jdbctemplate;

import demo10_jdbctemplate.entity.Student;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    }
}
