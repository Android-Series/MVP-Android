package com.jxq.mvp.dataline;

import android.util.Log;

import com.jxq.mvp.common.databus.DataBusSubscriber;
import com.jxq.mvp.common.databus.RegisterBus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liuguangli on 17/5/17.
 */

public class RxLine {

    private static final String TAG = "RxLine";
    private static volatile RxLine instance;
    // 订阅者集合：只要是DataBusSubscriber类型，都可以作为订阅者，观察者模式核心代码
    private Set<DataBusSubscriber> subscribers; //这就是我们刚定义的接口，我们用集合的成员变量把它装起来，装起来我们要注册

    /**
     *  注册 DataBusSubscriber
     * @param subscriber
     */
    public synchronized void register(DataBusSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    /**
     *  注销 DataBusSubscriber
     * @param subscriber
     */
    public synchronized void unRegister(DataBusSubscriber subscriber) {
        subscribers.remove(subscriber);
    }


    /**
     *  单利模式
     */
    private RxLine() {
        subscribers = new CopyOnWriteArraySet<>();
    }
    public static synchronized RxLine getInstance() {

        if (instance == null) {
            synchronized (RxLine.class) {
                if (instance == null) {
                    instance = new RxLine();
                }

            }
        }
        return instance;
    }

    /**
     * 包装处理过程
     * @param func
     */
    public void chainProcess(Func1 func) {
        Observable.just("") //利用它的Map属性
                .subscribeOn(Schedulers.io()) // 指定处理过程在 IO 线程，选择IO线程池做线程的管理

                .map(func)   // 把处理过程放到子线程中去做（model层做），包装处理过程，把处理过程作为一个参数func传过来，把处理的过程放在IO线程里
                .observeOn(AndroidSchedulers.mainThread())  // 指定事件消费在 Main 线程
                .subscribe(new Action1<Object>() {
                    //处理完成之后，要通知presenter
                    // call方法执行通知的过程，这是放在UI线程里的
                    @Override
                    public void call(Object data) {

                        Log.d(TAG,"chainProcess start");
                        //递归我们刚刚传过来的订阅者
                        for(DataBusSubscriber s:subscribers){
                            //数据发送到注册的 DataBusSubscriber
                            s.onEvent(data);//把处理之后的数据返回，回调到这个方法里面
                        }

                    }
                });
    }
}
