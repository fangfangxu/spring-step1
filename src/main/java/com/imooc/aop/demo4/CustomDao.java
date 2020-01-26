package com.imooc.aop.demo4;

/**
 * Spring基于切入点的切面demo
 */
public class CustomDao {
    public void find() {
        System.out.println("**********查询客户**********");
    }

    public void save() {
        System.out.println("**********保存客户**********");
    }

    public void update() {
        System.out.println("**********修改客户**********");
    }

    public void delete() {
        System.out.println("**********删除客户**********");
    }
}
