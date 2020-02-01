package demo11_tx.service.impl6;

import demo11_tx.dao.OrderDao;
import demo11_tx.dao.ProductDao;
import demo11_tx.entity.Order;
import demo11_tx.entity.Product;
import demo11_tx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 声明式事务管理：基于AOP
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;

    @Transactional(propagation=Propagation.REQUIRED)
    @Override
    public void addOrder(Order order) {
            //第一步 生成订单
            orderDao.insert(order);
            //第二步 修改库存
            Product product = productDao.select(order.getProductsId());
            product.setStock(product.getStock() - order.getNumber());
            productDao.update(product);
            //提交事务
    }
}
