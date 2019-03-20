package com.jxq.mvp.account.view;

import android.view.View;

import com.jxq.mvp.R;

/**
 * 注册页面的抽象接口：那这个view有哪些交互逻辑呢？
 */

public interface ICreatePasswordDialogView extends IView {

    /**
     * 显示注册成功
     */
    void showRegisterSuc();

    /**
     * 显示登录成功
     */
    void showLoginSuc();

    /**
     * 显示密码为空
     */
    void showPasswordNull();

    /**
     *  显示两次输入密码不一样
     */
    void showPasswordNotEqual();
}