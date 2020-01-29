package demo10_jdbctemplate;

import demo10_jdbctemplate.dao.CourseDao;
import demo10_jdbctemplate.dao.SelectionDao;
import demo10_jdbctemplate.dao.StudentDao;
import demo10_jdbctemplate.entity.Course;
import demo10_jdbctemplate.entity.Selection;
import demo10_jdbctemplate.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class jdbcTest2 {
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private SelectionDao selectionDao;


    public void test1() {
        Student stu1 = new Student(1, "张三", "男", new Date());
        Student stu2 = new Student(2, "李四", "女", new Date());
//       studentDao.insert(stu1);
//       studentDao.insert(stu2);
        Student student = studentDao.select(4);
//       List<Student> student=  studentDao.selectAll();
        student.setName("2222");
        studentDao.update(student);
        student = studentDao.select(4);
        System.out.println(student);
    }


    public void test2() {
        Course c1 = new Course(1, "语文", 50);
        Course c2 = new Course(2, "数学", 50);
        Course c3 = new Course(3, "英语", 50);
//        courseDao.insert(c1);
//        courseDao.insert(c2);
//        courseDao.insert(c3);
        List<Course> courses = courseDao.selectAll();
        System.out.println(courses);
        Course course = courseDao.select(1006);
        course.setName("英语11");
        courseDao.update(course);
        course = courseDao.select(1006);
        courseDao.delete(1006);
        courses = courseDao.selectAll();
        System.out.println(courses);
    }

    @Test
    public void test3() {
//        List<Selection> selections=new ArrayList<>();
//        Selection s1=new Selection(1,1004, new Date(),100);
//        Selection s2=new Selection(1,1005, new Date(),100);
//        selections.add(s1);
//        selections.add(s2);
//        selectionDao.insert(selections);
        List<Map<String, Object>> result1 = selectionDao.selectByCourse(1004);
        List<Map<String, Object>> result2 = selectionDao.selectByStudent(1);
        System.out.println(result1);
        System.out.println(result2);
    }

}
