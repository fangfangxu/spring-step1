package com.imooc.aop.demo3;

/**
 * Spring一般切面实现：不用手写底层jdk动态代理
 * （1）接口+实现类
 * （2）自定义前置通知
 * （3）applicationContext.xml中配置bean，配置一般切面
 */
public class StudentDaoImpl implements StudentDao {
    @Override
    public void find() {
        System.out.println("****Spring一般切面--查询****");
    }

    @Override
    public void save() {
        System.out.println("****Spring一般切面--保存****");
    }

    @Override
    public void update() {
        System.out.println("****Spring一般切面--更新****");
    }

    @Override
    public void delete() {
        System.out.println("****Spring一般切面--删除****");
    }
}
