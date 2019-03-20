package com.jxq.mvp.account.view;

/**
 * 1、定义一个顶级接口：通用的方法提取到顶级接口中去
 * 2、接口之间是继承关系
 */

public interface IView {
    /**
     * 显示loading
     */
    void showLoading();
    /**
     *  显示错误
     */
    void showError(int Code, String msg);
}
