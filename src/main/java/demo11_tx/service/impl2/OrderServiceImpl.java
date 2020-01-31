package demo11_tx.service.impl2;

import demo11_tx.dao.OrderDao;
import demo11_tx.dao.ProductDao;
import demo11_tx.entity.Order;
import demo11_tx.entity.Product;
import demo11_tx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void addOrder(final Order order) {
        transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                try {
                    //第一步 生成订单
                    orderDao.insert(order);
                    //第二步 修改库存
                    Product product = productDao.select(order.getProductsId());
                    product.setStock(product.getStock() - order.getNumber());
                    productDao.update(product);
                } catch (Exception e) {
                    //事务回滚
                    e.printStackTrace();
                    transactionStatus.setRollbackOnly();
                }
                return null;
            }
        });
    }
}
