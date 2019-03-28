package com.jxq.mvp;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
/**
 * RxJava测试
 */
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
                //subscriber1.onError(new Exception("error"));//该方法调用的时候，下面代码停止执行
                subscriber1.onNext("hello world");
                subscriber1.onCompleted();
            }
        });
        //使一个动作使它们产生联系，就是订阅这个动作
        /**
         * 当这个方法被调用的是时候，call方法才会被触发，接下来观察者下的回调方法被触发
         * 在我们的app中，一般场景下就发生在我们的工作子线程里面
         * 这三个方法是在同一个子线程中执行的
         */
        observable.subscribe(subscriber);
    }
    /**
     * 产生事件的call方法的调用和观察者的回调产生在不同的线程，如何做？
     * 在实际的应用中，观察者通常是发生在UI线程里面
     * 数据处理通常是发送在子线程里面
     * 所以rxjava为我们提供了Scheduler API处理线程切换、线程调度的问题
     */
    @Test
    public void testScheduler() {
        //观察者
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
        observable.subscribeOn(Schedulers.io())  // 指定生产事件在当前 线程中进行，也就是call方法在哪个线程调用
                .observeOn(Schedulers.newThread()) //  指定消费事件在新线程中进行
                .subscribe(subscriber);
    }
    // map
    @Test
    public void testMap() {
        String name = "dalimao";
        //最初的输入是一个字符串：name,表示通过名字查询出一个对象的过程，也就是我们的call方法传入的是一个字符串
        Observable.just(name)
                //第一个节点
                .subscribeOn(Schedulers.newThread()) // 指定下一个生成节点在新线程中处理，下一个节点是什么呢，就是map
                .map(new Func1<String, User>() {  //每个map都是一个处理的过程，map传入的是Function这个方法，方法里面有一个泛型，第一个代表数据的输入，第二个代表输出，也就是我们返回的数据是什么
                    /**
                     * 由最初的字符串，变成了User对象
                     * @param name
                     * @return
                     */
                    @Override
                    public User call(String name) {
                        User user = new User();
                        user.setName(name);
                        System.out.println("process User call in tread:" + Thread.currentThread().getName());
                        return user;
                    }
                })

                //第二个节点：下一节点的输入就是上一个节点的输出
                .subscribeOn(Schedulers.newThread()) // 指定下一个生产节点在新线程中处理
                .map(new Func1<User, Object>() { //User对象是上一个节点传入的
                    @Override
                    public Object call(User user) {
                        // 如果需要，我们在这里还可以对 User 进行加工，在这里我们什么也没做直接返回，每一个节点可以对上一个节点传入的数据进行加工
                        // 像流水线一样
                        System.out.println("process User call in tread:" + Thread.currentThread().getName());
                        return user;
                    }
                })

                //第三个节点：消费者最终消费的节点，消费节点也就是观察者线程最终回调的节点
                .observeOn(Schedulers.newThread()) // 指定消费节点在新线程中处理
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object data) {
                        //这里只是打印一下它的线程
                        System.out.println("receive User call in tread:" + Thread.currentThread().getName());
                    }
                });
    }

    // map
    @Test
    public void testMap2() {

        String name = "dalimao";
        //最初的输入是一个字符串：name,表示通过名字查询出一个对象的过程，也就是我们的call方法传入的是一个字符串
        Observable.just(name)
                //第一个节点
                .subscribeOn(Schedulers.newThread()) // 指定下一个生成节点在新线程中处理，下一个节点是什么呢，就是map
                .map(new Func1<String, User>() {  //每个map都是一个处理的过程，map传入的是Function这个方法，方法里面有一个泛型，第一个代表数据的输入，第二个代表输出，也就是我们返回的数据是什么
                    /**
                     * 由最初的字符串，变成了User对象
                     * @param name
                     * @return
                     */
                    @Override
                    public User call(String name) {
                        User user = new User();
                        user.setName(name);
                        System.out.println("process User call in tread:" + Thread.currentThread().getName());
                        return user;
                    }
                })

                //第二个节点：下一节点的输入就是上一个节点的输出
                .subscribeOn(Schedulers.newThread()) // 指定下一个生产节点在新线程中处理
                .map(new Func1<User, Object>() { //User对象是上一个节点传入的
                    @Override
                    public Object call(User user) {
                        // 如果需要，我们在这里还可以对 User 进行加工，在这里我们什么也没做直接返回，每一个节点可以对上一个节点传入的数据进行加工
                        // 像流水线一样
                        System.out.println("process User call in tread:" + Thread.currentThread().getName());
                        return user;
                    }
                })

                //第三个节点：加入rx anddroid API
                .observeOn(AndroidSchedulers.mainThread()) // 消费线程指定在Android线程里面,主线程里面,但是这里是运行不起来的
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object data) {
                        //这里只是打印一下它的线程
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
