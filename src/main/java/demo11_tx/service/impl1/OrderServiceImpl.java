package demo11_tx.service.impl1;

import demo11_tx.dao.OrderDao;
import demo11_tx.dao.ProductDao;
import demo11_tx.entity.Order;
import demo11_tx.entity.Product;
import demo11_tx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

@Service
public class OrderServiceImpl implements OrderService {
@Autowired
private OrderDao orderDao;
@Autowired
private ProductDao productDao;
@Autowired
private PlatformTransactionManager platformTransactionManager;
@Autowired
private TransactionDefinition transactionDefinition;


    @Override
    public void addOrder(Order order) {




    }
}
