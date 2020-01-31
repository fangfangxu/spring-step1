package demo11_tx;

import demo11_tx.entity.Order;
import demo11_tx.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Spring基于底层API的事务实现
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-service1.xml")
public class OrderServiceTest1 {
    @Autowired
    private OrderService orderService;

    @Test
    public void testAddOrder(){
        Order order=new Order("100007","100002",2,1799,"联系人","13000000000","");
        orderService.addOrder(order);
    }




}
