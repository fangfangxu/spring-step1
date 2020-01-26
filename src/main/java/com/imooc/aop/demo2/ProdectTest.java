package com.imooc.aop.demo2;

import org.junit.Test;

public class ProdectTest {
    @Test
    public void test1(){
        ProductDao productDao=new ProductDao();
        MyCglibProxy myCglibProxy=new MyCglibProxy(productDao);
        ProductDao productProxy=(ProductDao)myCglibProxy.createProxy();
        productProxy.delete();
        productProxy.find();
        productProxy.update();
        productProxy.save();

    }
}
