package com.jxq.mvp.account.presenter;

/**
 * 为UI层提供的接口：具体的下发请求过程，以及这个请求结果的处理怎么去反馈到我们的UI层？
 * 这些是放在我们的实现中去完成的
 *
 */
public interface ISmsCodeDialogPresenter {


    /**
     *  请求下发验证码
     */
    void requestSendSmsCode(String phone);
    /**
     * 请求校验验证码
     */
    void requestCheckSmsCode(String phone, String smsCode);
    /**
     * 用户是否存在
     */
    void requestCheckUserExist(String phone);
}
