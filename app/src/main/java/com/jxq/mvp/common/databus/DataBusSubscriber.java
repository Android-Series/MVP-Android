package com.jxq.mvp.common.databus;

/**
 * 数据订阅者，Presenter 要实现这个接口来接收数据
 */
public interface DataBusSubscriber {
    /**
     * 当我们在model层处理完数据之后，会回调这个方法
     * @param data
     */
    void onEvent(Object data);
}
