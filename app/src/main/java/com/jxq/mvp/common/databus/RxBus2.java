package com.jxq.mvp.common.databus;

import android.util.Log;

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

public class RxBus2 {

    private static final String TAG = "RxBus";
    private static volatile RxBus2 instance;
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
    private RxBus2() {
        subscribers = new CopyOnWriteArraySet<>();
    }
    public static synchronized RxBus2 getInstance() {

        if (instance == null) {
            synchronized (RxBus2.class) {
                if (instance == null) {
                    instance = new RxBus2();
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

    /**
     * 发送数据
     * @param data
     */
    public void send(Object data) {
        //循环观察者：递归传过来的订阅者
        for (Object subscriber : subscribers) {
            // 扫描注解，将数据发送到注册的对象的标记方法
            callMethodByAnnotiation(subscriber, data);//调用观察者的方法
        }
    }


    /**
     * 反射获取对象方法列表，判断：
     * 1 是否被注解修饰
     * 2 参数类型是否和 data 类型一致
     * @param target
     * @param data
     */

    private void callMethodByAnnotiation(Object target, Object data) {

        Method[] methodArray = target.getClass().getDeclaredMethods();//反射

        for (int i = 0; i < methodArray.length; i++) {
            try {
                if (methodArray[i].isAnnotationPresent(RegisterBus.class)) {
                    // 被 @RegisterBus 修饰的方法
                    Class paramType = methodArray[i].getParameterTypes()[0]; //拿到参数类型
                    if (data.getClass().getName().equals(paramType.getName())) {
                        // 参数类型和 data 一样，调用此方法
                        methodArray[i].invoke(target, new Object[]{data});//通过反射调用
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
