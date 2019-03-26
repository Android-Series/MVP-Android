package com.jxq.mvp;

/**
 * Created by liuguangli on 17/3/5.
 */

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class TestRxJava {
    @Before
    public void setUp() {
        Thread.currentThread().setName("currentThread");
    }

    /**
     * 第一个例子：认识一下什么是观察者
     */
    @Test
    public void testSubscribe() {
        //观察者
        final Subscriber<String> subscriber = new Subscriber<String>() {
            //这三个回调都在里面打印了一些信息
            @Override
            public void onCompleted() {
                System.out.println("onCompleted in tread:" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError in tread:" + Thread.currentThread().getName());
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext in tread:" + Thread.currentThread().getName());
                System.out.println(s);
            }
        };

        //被观察者
        Observable observable = Observable.create(new Observable.OnSubscribe<Subscriber>() {
            @Override
            public void call(Subscriber subscriber1) {
                // 发生事件
                System.out.println("call in tread:" + Thread.currentThread().getName());
                subscriber1.onStart();
                subscriber1.onError(new Exception("error"));//该方法调用的时候，下面代码停止执行
                subscriber1.onNext("hello world");
                subscriber1.onCompleted();
            }
        });
        //使一个动作使它们产生联系，就是订阅这个动作
        observable.subscribe(subscriber);//当这个方法被调用的是时候，call方法才会被触发，接下来观察者下的回调方法被触发
    }

    @Test
    public void testScheduler() {

        //观察者／订阅者
        final Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted in tread:" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError in tread:" + Thread.currentThread().getName());
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext in tread:" + Thread.currentThread().getName());
                System.out.println(s);
            }
        };

        //被观察者
        Observable observable = Observable.create(new Observable.OnSubscribe<Subscriber>() {
            @Override
            public void call(Subscriber subscriber1) {
                // 发生事件
                System.out.println("call in tread:" + Thread.currentThread().getName());
                subscriber1.onStart();
                subscriber1.onNext("hello world");
                subscriber1.onCompleted();

            }
        });

        //订阅
        observable.subscribeOn(Schedulers.io())  // 指定生产事件在当前 线程中进行
                .observeOn(Schedulers.newThread()) //  指定消费事件在新线程中进行
                .subscribe(subscriber);


    }

    // map
    @Test
    public void testMap() {

        String name = "dalimao";
        Observable.just(name)
                .subscribeOn(Schedulers.newThread()) // 指定下一个生成节点在新线程中处理
                .map(new Func1<String, User>() {
                    @Override
                    public User call(String name) {
                        User user = new User();
                        user.setName(name);
                        System.out.println("process User call in tread:" + Thread.currentThread().getName());
                        return user;
                    }
                })

                .subscribeOn(Schedulers.newThread()) // 指定下一个生产节点在新线程中处理
                .map(new Func1<User, Object>() {
                    @Override
                    public Object call(User user) {
                        // 如果需要，我们在这里还可以对 User 进行加工
                        System.out.println("process User call in tread:" + Thread.currentThread().getName());
                        return user;
                    }
                })

                .observeOn(Schedulers.newThread()) // 指定消费节点在新线程中处理
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object data) {
                        System.out.println("receive User call in tread:" + Thread.currentThread().getName());
                    }
                });
    }


    public static class User {
        String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
