package demo10_jdbctemplate.dao.impl;

import demo10_jdbctemplate.dao.CourseDao;
import demo10_jdbctemplate.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class CourseDaoImpl implements CourseDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Course course) {
        String sql="insert into course(name,score) values(?,?)";
        jdbcTemplate.update(sql,course.getName(),course.getScore());
    }

    @Override
    public void update(Course course) {
        String sql="update course set name=?,score=? where id=?";
        jdbcTemplate.update(sql,course.getName(),course.getScore(),course.getId());
    }

    @Override
    public void delete(int id) {
        String sql="delete course where id=?";
        jdbcTemplate.update(sql,id);
    }

    @Override
    public Course select(int id) {
        String sql="select * from course where id=?";
        Course course = jdbcTemplate.queryForObject(sql,new Object[]{id},new CourseRowMapper());
        return course;
    }

    @Override
    public List<Course> selectAll() {
        String sql="select * from course";
        List<Course> courses =jdbcTemplate.query(sql,new CourseRowMapper());
        return courses;
    }

    /**
     * 私有内部类：处理复杂对象查询与实体类映射问题
     */
    private class CourseRowMapper implements RowMapper<Course> {
        @Override
        public Course mapRow(ResultSet resultSet, int i) throws SQLException {
            Course course=new Course();
            course.setId(resultSet.getInt("id"));
            course.setName(resultSet.getString("name"));
            course.setScore(resultSet.getInt("score"));
            return course;
        }
    }
}
