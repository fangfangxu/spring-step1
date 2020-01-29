package demo10_jdbctemplate.dao;

import demo10_jdbctemplate.entity.Student;

import java.util.List;

public interface StudentDao {
    void insert(Student stu);

    void update(Student stu);

    void delete(int id);

    Student select(int id);

    List<Student> selectAll();
}
