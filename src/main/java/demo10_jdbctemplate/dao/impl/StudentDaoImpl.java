package demo10_jdbctemplate.dao.impl;

import demo10_jdbctemplate.dao.StudentDao;
import demo10_jdbctemplate.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StudentDaoImpl implements StudentDao {
   @Autowired
   private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Student stu) {
     String sql="insert into student(name,sex,born) values(?,?,?)";
     jdbcTemplate.update(sql,stu.getName(),stu.getSex(),stu.getBorn());
    }

    @Override
    public void update(Student stu) {
     String sql="update student set name=?,sex=?,born=? where id=?";
     jdbcTemplate.update(sql,stu.getName(),stu.getSex(),stu.getBorn(),stu.getId());
    }

    @Override
    public void delete(int id) {
        String sql="delete student where id=?";
        jdbcTemplate.update(sql,id);

    }

    @Override
    public Student select(int id) {
        String sql="select * from student where id=?";
        Student stu = jdbcTemplate.queryForObject(sql,new Object[]{id},new StudentRowMapper());
        return stu;
    }

    @Override
    public List<Student> selectAll() {
        String sql="select * from student";
        List<Student> stu =jdbcTemplate.query(sql,new StudentRowMapper());
        return stu;
    }

    /**
     * 私有内部类：处理复杂对象查询与实体类映射问题
     */
  private class StudentRowMapper implements RowMapper<Student>{
        @Override
        public Student mapRow(ResultSet resultSet, int i) throws SQLException {
            Student student=new Student();
            student.setId(resultSet.getInt("id"));
            student.setName(resultSet.getString("name"));
            student.setSex(resultSet.getString("sex"));
            student.setBorn(resultSet.getDate("born"));
            return student;
        }
    }

}
