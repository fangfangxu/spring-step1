package demo11_tx;

import demo11_tx.entity.Order;
import demo11_tx.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 声明式事务管理-基于TransactionInterceptor
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-service4.xml")
public class OrderServiceTest4 {

    @Autowired
    private OrderService orderService;

    @Test
    public void testAddOrder(){
        Order order=new Order("100009","100002",2,1799,"联系人","13000000000","");
        orderService.addOrder(order);
    }

}
