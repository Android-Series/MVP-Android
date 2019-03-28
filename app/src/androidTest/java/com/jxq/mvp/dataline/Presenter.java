package com.jxq.mvp.dataline;

import android.util.Log;

import com.jxq.mvp.common.databus.DataBusSubscriber;
import com.jxq.mvp.databus.RxBusTest;

/**
 *  模拟 Presenter，实现 DataBusSubscriber 接口，接收数据
 */
public class Presenter implements DataBusSubscriber {

    private Manager manager;
    public Presenter(Manager manager) {
        this.manager = manager;
    }
    public void getUser() {
        manager.getUser(); //通过manager对象向下传递指令
    }
    public void getOrder() {
        manager.getOrder();
    }
    /**
     * 1、获取model传递过来的数据，是object类型
     * 2、接下来再去判断具体的数据类型，是属于User还是Order
     * 3、相比于Hander简单来很多，不用定义Handelr了，也不要定义什么内部类了
     * 只要定义一个实现方法，接收数据，然后根据不同的数据类型去做不同的业务处理，presenter的写法进一步的优雅了
     * @param data
     */
    @Override
    public void onEvent(Object data) {
        if(data instanceof User){
            Log.d(RxBusTest.TAG,"receive User in thread:"+Thread.currentThread());
        }else if(data instanceof Order){
            Log.d(RxBusTest.TAG,"receiver Order :"+Thread.currentThread());
        }else {
            Log.d(RxBusTest.TAG,"receiver data :"+Thread.currentThread());
        }
    }
}
