package com.jxq.mvp.databus;

import android.util.Log;

import com.jxq.mvp.common.databus.DataBusSubscriber;
import com.jxq.mvp.common.databus.RegisterBus;
import com.jxq.mvp.common.databus.RxBus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.functions.Func1;

/**
 * rx java 接口的实现方式
 */
public class RxBusTest2 {

    public static final String TAG = "RxBusTest";
    Presenter presenter;

    @Before
    public void setUp() throws Exception {
        /**
         *  初始化 presenter 并注册
         */
        presenter = new Presenter(new Manager());
        RxBus.getInstance().register(presenter);//把presenter作为一个订阅者，注册进去
    }

    @After
    public void tearDown() {
        try {
            Thread.sleep(5000);//作用：因为测试是在单线程执行的，但这里涉及到多线程，所以这里保持5秒中，去等待，去模拟正常的activity生命周期
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RxBus.getInstance().unRegister(presenter);
    }

    /**
     * 两个测试方法，获取User，获取Order
     * @throws Exception
     */
    @Test
    public void testGetUser() throws Exception {

        presenter.getUser();
    }

    @Test
    public void testGetOrder() throws Exception {
        presenter.getOrder();
    }

}


/**
 *  模拟 Presenter
 */
class Presenter2 implements DataBusSubscriber {

    private Manager manager;

    public Presenter2(Manager manager) {
        this.manager = manager;
    }

    public void getUser() {
        manager.getUser(); //通过manager对象向下传递指令
    }

    public void getOrder() {
        manager.getOrder();
    }

    /**
     * 实现 DataBusSubscriber 接口，接收数据
     * 获取model传递过来的数据，是object类型
     * 接下来再去判断具体的数据类型，是属于User还是Order
     *
     * 相比于Hander简单来很多，不用定义Handelr了，也不要定义什么内部类了
     * 只要定义一个实现方法，接收数据，然后根据不同的数据类型去做不同的业务处理，presenter的写法进一步的优雅了
     * @param data
     */
    @Override
    public void onEvent(Object data) {
        if(data instanceof  User){
            Log.d(RxBusTest.TAG,"receive User in thread:"+Thread.currentThread());
        }else if(data instanceof Order){
            Log.d(RxBusTest.TAG,"receiver Order :"+Thread.currentThread());
        }else {
            Log.d(RxBusTest.TAG,"receiver data :"+Thread.currentThread());
        }
    }
}

/**
 *  模拟 MODEL 层处理
 *  Model层的写法也更优雅了：不用再去创建线程，只要包装一个处理过程进去就行了
 */
class Manager2 {
    public void getUser () {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                Log.d(RxBusTest.TAG, "chainProcess getUser start in thread:" + Thread.currentThread());
                User user = new User(); //模拟的对象
                try {
                    Thread.sleep(1000);//模拟耗时1秒这个过程
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 把 User 数据到 Presenter
                return user;
            }
        });
    }

    public void getOrder() {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                Log.d(RxBusTest.TAG, "chainProcess getOrder start in thread::" + Thread.currentThread());
                Order order = new Order(); //模拟的

                // 把 order 数据到 Presenter
                return order;
            }
        });
    }
}

/**
 *  要返回的数据类型
 */

class User2 {

}

class Order2 {

}