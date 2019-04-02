package com.jxq.mvp.common.databus;

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
 * RxBus 重构代码，让MVP更优雅
 * 观察者模式
 */

public class RxBus {

    private static final String TAG = "RxBus";
    private static volatile RxBus instance;
    // 订阅者集合
    private Set<Object> subscribers;//只要是object类型，都可以作为订阅者，观察者模式核心代码

    /**
     *  注册 DataBusSubscriber
     * @param subscriber
     */
    public synchronized void register(Object subscriber) {
        subscribers.add(subscriber);
    }
    /**
     *  注销 DataBusSubscriber
     * @param subscriber
     */
    public synchronized void unRegister(Object subscriber) {
        subscribers.remove(subscriber);
    }
    /**
     *  单利模式
     */
    private RxBus() {
        subscribers = new CopyOnWriteArraySet<>();
    }
    public static synchronized RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
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
                .subscribeOn(Schedulers.io()) // 指定处理过程在 IO 线程

                .map(func)   // 包装处理过程，把处理过程作为一个参数func传过来，把处理的过程放在IO线程里
                .observeOn(AndroidSchedulers.mainThread())  // 指定事件消费在 Main 线程
                .subscribe(new Action1<Object>() {
                    //call方法执行通知的过程，这是放在UI线程里的
                    @Override
                    public void call(Object data) {
                        if (data == null) {
                            return;
                        }
                        send(data);//通知所有的订阅者
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
            // 扫描注解，将数据发送到注册的对象被的标记方法里面去
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
