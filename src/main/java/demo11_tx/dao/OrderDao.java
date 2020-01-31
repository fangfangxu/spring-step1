package demo11_tx.dao;



import demo11_tx.entity.Order;

import java.util.List;

public interface OrderDao {
    void insert(Order order);
    void update(Order order);
    void delete(String id);
    Order select(String id);
    List<Order> select();
}
