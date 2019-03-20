package com.jxq.mvp.account.presenter;

/**
 * 登陆框对应的presenter.
 */

public interface ILoginDialogPresenter {
    /**
     * 登录
     */
    void requestLogin(String phone, String password);
}
