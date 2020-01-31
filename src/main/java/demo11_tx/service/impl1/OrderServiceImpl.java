package demo11_tx.service.impl1;

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


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    @Qualifier("transactionDefination")
    private TransactionDefinition transactionDefinition;

    @Override
    public void addOrder(Order order) {
        //开启事务/获取事务
        TransactionStatus ts = platformTransactionManager.getTransaction(transactionDefinition);
        try {
            //第一步 生成订单
            orderDao.insert(order);
            //第二步 修改库存
            Product product = productDao.select(order.getProductsId());
            product.setStock(product.getStock() - order.getNumber());
            productDao.update(product);
            //提交事务
            platformTransactionManager.commit(ts);
        } catch (Exception e) {
            //事务回滚
            e.printStackTrace();
            platformTransactionManager.rollback(ts);
        }
    }
}
