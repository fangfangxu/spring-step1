package com.imooc.aop.demo6;

public class UserImpl implements User {
    @Override
    public void find() {
        System.out.println("===UserImpl===查询");
    }

    @Override
    public void save() {
        System.out.println("===UserImpl===保存");
    }
}
