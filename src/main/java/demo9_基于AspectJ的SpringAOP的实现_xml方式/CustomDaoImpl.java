package demo9_基于AspectJ的SpringAOP的实现_xml方式;

/**
 * 配置目标类
 */
public class CustomDaoImpl implements CustomDao {
    @Override
    public void save() {
        System.out.println("保存客户....");
    }

    @Override
    public String delete() {
        System.out.println("删除客户....");
        return "delete";
    }

    @Override
    public void update() {
        System.out.println("更新客户....");
    }

    @Override
    public void findOne() {
        System.out.println("查询一个客户....");
    }

    @Override
    public void findAll() {
        System.out.println("查询所有客户....");
    }
}
