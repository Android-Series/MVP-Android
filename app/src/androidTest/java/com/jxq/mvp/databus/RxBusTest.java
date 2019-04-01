package com.jxq.mvp.databus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Rxjava 通过注解的方式
 * 注解的本质：反射
 */
public class RxBusTest {

    public static final String TAG = "RxBusTest";
    Presenter presenter;

    @Before
    public void setUp(){
        presenter = new Presenter(new Manager()); //初始化 presenter 并注册
        RxBus.getInstance().register(presenter);
    }

    @After
    public void tearDown() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RxBus.getInstance().unRegister(presenter);
    }

    @Test
    public void testGetUser() throws Exception {

        presenter.getUser();
    }

    @Test
    public void testGetOrder() throws Exception {
        presenter.getOrder();
    }
}







