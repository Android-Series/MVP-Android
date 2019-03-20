package com.jxq.mvp.account.view;


/**
 * 定义接口的时候要考虑到：这个view是干什么的，给谁用的
 *
 * 验证码输入框有哪些交互逻辑呢？
 */

public interface ISmsCodeDialogView extends IView {


    /**
     * 显示倒计时
     */
    void showCountDownTimer();


    /**
     * 显示验证状态
     * @param b
     */
    void showSmsCodeCheckState(boolean b);


    /**
     * 用户是否存在
     * @param b
     */
    void showUserExist(boolean b);
}
