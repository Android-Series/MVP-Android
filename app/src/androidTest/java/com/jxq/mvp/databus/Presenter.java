package com.jxq.mvp.databus;

import android.util.Log;
/**
 *  模拟 Presenter
 */
class Presenter  {
    private Manager manager;

    public Presenter(Manager manager) {
        this.manager = manager;
    }

    public void getUser() {
        manager.getUser();
    }

    public void getOrder() {
        manager.getOrder();
    }

    /**
     * 被这个注解的方法是可以接收数据的
     * @param user
     */
    @RegisterBus
    public void onUser(User user) {
        Log.d(RxBusTest.TAG , "receive User in thread:" + Thread.currentThread());
    }

    @RegisterBus
    public void onOrder(Order order) {
        Log.d(RxBusTest.TAG , "receive order in thread:" + Thread.currentThread());
    }
}
