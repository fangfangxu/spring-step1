package demo10_jdbctemplate.dao;


import demo10_jdbctemplate.entity.Course;

import java.util.List;

public interface CourseDao {
    void insert(Course course);

    void update(Course course);

    void delete(int id);

    Course select(int id);

    List<Course> selectAll();
}
