package com.jxq.mvp.dataline;

/**
 * 数据订阅者，Presenter 要实现这个接口，去实现onEvent()方法
 * 当我们在model层处理完数据之后，我们会回调onEvent()方法来传递数据
 * 这个接口非常简单，是一个订阅者
 * 来接收数据
 */
public interface DataBusSubscriber {
    /**
     * 当我们在model层处理完数据之后，会回调这个方法
     * @param data
     */
    void onEvent(Object data);
}
