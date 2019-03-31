package com.jxq.mvp.dataline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试RX Java的使用：通过接口实现
 */
public class RxLineTest {
    public static final String TAG = "RxLineTest";
    Presenter presenter;
    @Before
    public void setUp(){
        //初始化 presenter 并注册，presenter就是我们的观察者
        presenter = new Presenter(new Manager());//将model层对象包装到presenter层对象当中
        RxLine.getInstance().register(presenter);//把presenter作为一个订阅者，注册进去
    }
    @After
    public void tearDown() {
        try {
            Thread.sleep(5000);//作用：因为测试是在单线程执行的，但这里涉及到多线程，所以这里保持5秒中，去等待，去模拟正常的activity生命周期
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RxLine.getInstance().unRegister(presenter); //从Set集合中移除观察者
    }
    @Test
    public void testGetUser(){
        presenter.getUser(); //测试方法一：获取User
    }
    @Test
    public void testGetOrder(){
        presenter.getOrder(); //测试方法二：获取Order
    }
}
