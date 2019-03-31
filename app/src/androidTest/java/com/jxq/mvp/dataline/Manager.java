package com.jxq.mvp.dataline;
import android.util.Log;
import com.jxq.mvp.databus.RxBusTest;
import rx.functions.Func1;
/**
 * 模拟 MODEL 层处理
 * Model层的写法也更优雅了：不用再去创建线程，只要包装一个处理过程进去就行了
 */
public class Manager {

    public void getUser() {
        RxLine.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                Log.d(RxLineTest.TAG, "chainProcess getUser start in thread:" + Thread.currentThread());
                User user = new User(); //模拟的对象
                try {
                    Thread.sleep(1000);//模拟耗时1秒这个过程
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return user; // 把 User 数据返回到 Presenter
            }
        });
    }

    public void getOrder() {
        RxLine.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                Log.d(RxLineTest.TAG, "chainProcess getOrder start in thread::" + Thread.currentThread());
                Order order = new Order(); //模拟的处理对象
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return order;// 把 order 数据到 Presenter
            }
        });
    }
}
