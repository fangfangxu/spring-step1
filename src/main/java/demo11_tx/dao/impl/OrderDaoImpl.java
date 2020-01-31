package demo11_tx.dao.impl;


import demo11_tx.dao.OrderDao;
import demo11_tx.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void insert(Order order) {
        String sql = "insert into orders values(?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,order.getId(),order.getProductsId(),order.getNumber(),order.getPrice(),order.getCreateTime(),order.getSendTime(),order.getConfirmTime(),order.getConsignee(),order.getConsigneePhone(),order.getConsigneeAddress(),order.getStatus());
    }

    public void update(Order order) {
        String sql = "update orders set create_time=?,send_time=?,confirm_time=?,consignee=?,consignee_phone=?,consignee_address=?,status=? where id=?";
        jdbcTemplate.update(sql,order.getProductsId(),order.getNumber(),order.getPrice(),order.getCreateTime(),order.getSendTime(),order.getConfirmTime(),order.getConsignee(),order.getConsigneePhone(),order.getConsigneeAddress(),order.getStatus(),order.getId());
    }

    public void delete(String id) {
        String sql = "delete from orders where id=?";
        jdbcTemplate.update(sql,id);
    }

    public Order select(String id) {
        String sql = "select form orders where id=?";
        return jdbcTemplate.queryForObject(sql,new OrderRowMapper(),id);
    }

    public List<Order> select() {
        String sql = "select form orders";
        return jdbcTemplate.query(sql,new OrderRowMapper());
    }

    private class OrderRowMapper implements RowMapper<Order>{
        public Order mapRow(ResultSet resultSet, int i) throws SQLException {
            Order order=new Order();
            order.setId(resultSet.getString("id"));
            order.setProductsId(resultSet.getString("product_id"));
            order.setNumber(resultSet.getInt("number"));
            order.setPrice(resultSet.getDouble("price"));
            order.setCreateTime(resultSet.getTimestamp("create_time"));
            order.setSendTime(resultSet.getTimestamp("send_time"));
            order.setConfirmTime(resultSet.getTimestamp("confirm_time"));
            order.setConsignee(resultSet.getString("consignee"));
            order.setConsigneePhone(resultSet.getString("consignee_phone"));
            order.setConsigneeAddress(resultSet.getString("consignee_address"));
            order.setStatus(resultSet.getString("status"));
            return order;
        }
    }
}
