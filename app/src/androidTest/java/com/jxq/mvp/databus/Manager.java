package com.jxq.mvp.databus;

import android.util.Log;
import rx.functions.Func1;

/**
 *  模拟 MODEL 层处理
 */
class Manager {
    public void getUser () {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                Log.d(RxBusTest.TAG, "chainProcess getUser start in thread:" + Thread.currentThread());
                User user = new User();
                try {
                    Thread.sleep(1000); //模拟耗时1秒这个过程
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return user; //把 User数据返回到Presenter
            }
        });
    }

    public void getOrder() {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                Log.d(RxBusTest.TAG, "chainProcess getOrder start in thread::" + Thread.currentThread());
                Order order = new Order();
                // 把 order 数据到 Presenter
                return order;
            }
        });
    }
}
