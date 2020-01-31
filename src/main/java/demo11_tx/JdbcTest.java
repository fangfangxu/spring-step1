package demo11_tx;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class JdbcTest {
    private String driver = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/os?useUnicode=true&charactorEncoding=utf-8&serverTimezone=UTC";
    private String username = "root";
    private String password = "123456";

    @Test
    public void addOrder() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute("insert into orders values('100001','100001',2,2499,now(),null,null,'刘备','13000000000','成都','待发货')");
            statement.execute("update products set stock=stock-2 where id='100001'");
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
