package demo10_jdbctemplate.dao.impl;

import demo10_jdbctemplate.dao.SelectionDao;
import demo10_jdbctemplate.entity.Selection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class SelectionDaoImpl implements SelectionDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(List<Selection> selections) {
        String sql = "insert into selection values(?,?,?,?)";
        List<Object[]> list = new ArrayList<>();
        for (Selection s : selections) {
            list.add(new Object[]{s.getSid(), s.getCid(), s.getSelTime(), s.getScore()});
        }
        jdbcTemplate.batchUpdate(sql, list);

    }

    @Override
    public void delete(int sid, int cid) {
        String sql = "delete from selection where student=? and course=?";
        jdbcTemplate.update(sql, sid, cid);
    }

    @Override
    public List<Map<String, Object>> selectByStudent(int sid) {
        String sql = "select stu.name as stuname,c.name,se.* from selection se " +
                "left join  student stu on se.student=stu.id left join course c on c.id=se.course " +
                "where se.student=? ";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, sid);
        return list;
    }

    @Override
    public List<Map<String, Object>> selectByCourse(int cid) {
        String sql = "select stu.name as stuname,c.name,se.* from selection se " +
                "left join  student stu on se.student=stu.id left join course c on c.id=se.course " +
                "where se.course=? ";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, cid);
        return list;
    }
}
