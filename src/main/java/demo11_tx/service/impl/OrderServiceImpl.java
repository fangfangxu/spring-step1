package demo11_tx.service.impl;

import demo11_tx.dao.OrderDao;
import demo11_tx.dao.ProductDao;
import demo11_tx.entity.Order;
import demo11_tx.entity.Product;
import demo11_tx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * 声明式事务管理：基于AOP
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;

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
