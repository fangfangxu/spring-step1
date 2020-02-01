package demo11_tx;

import demo11_tx.entity.Order;
import demo11_tx.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 声明式事务管理-基于<tx>命名空间的声明式事务处理
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-service6.xml")
public class OrderServiceTest6 {

    @Autowired
    private OrderService orderService;

    @Test
    public void testAddOrder(){
        Order order=new Order("100013","100002",2,1799,"联系人","13000000000","");
        orderService.addOrder(order);
    }

}
