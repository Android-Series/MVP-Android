package com.jxq.mvp.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.jxq.mvp.account.model.IAccountManager;
import com.jxq.mvp.account.model.response.LoginResponse;
import com.jxq.mvp.account.view.ILoginView;
import com.jxq.mvp.common.databus.RegisterBus;

import java.lang.ref.WeakReference;



/**
 * Created by liuguangli on 17/5/14.
 */

public class LoginDialogPresenterImpl implements ILoginDialogPresenter {

    private ILoginView view;
    private IAccountManager accountManager;
    /**
     * 接收子线程消息的 Handler
     */
    static class MyHandler extends Handler {
        // 弱引用
        WeakReference<LoginDialogPresenterImpl> dialogRef;

        public MyHandler(LoginDialogPresenterImpl presenter) {
            dialogRef = new WeakReference<LoginDialogPresenterImpl>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginDialogPresenterImpl presenter = dialogRef.get();
            if (presenter == null) {
                return;
            }
            // 处理UI 变化
            switch (msg.what) {
                case IAccountManager.LOGIN_SUC:
                    // 登录成功
                    presenter.view.showLoginSuc();
                    break;
                case IAccountManager.PW_ERROR:
                   // 密码错误
                    presenter.view.showError(IAccountManager.PW_ERROR, "");
                    break;
                case IAccountManager.SERVER_FAIL:
                    // 服务器错误
                    presenter.view.showError(IAccountManager.SERVER_FAIL, "");
                    break;
            }
        }
    }


    public LoginDialogPresenterImpl(ILoginView view, IAccountManager accountManager) {
        this.view = view;
        this.accountManager = accountManager;
        accountManager.setHandler(new MyHandler(this));
    }

    /**
     * 登陆很简单，我们只有一个登陆的请求方法
     * @param phone
     * @param password
     */
    @Override
    public void requestLogin(String phone, String password) {
        accountManager.login(phone, password);//这里我们只是向下调用：登陆的方法
    }

    @RegisterBus
    public void onLoginResponse(LoginResponse response) {
        switch (response.getCode()) {
            case IAccountManager.LOGIN_SUC:
                // 登录成功
                view.showLoginSuc();
                break;
            case IAccountManager.PW_ERROR:
                // 密码错误
                view.showError(IAccountManager.PW_ERROR, "");
                break;
            case IAccountManager.SERVER_FAIL:
                // 服务器错误
               view.showError(IAccountManager.SERVER_FAIL, "");
                break;
        }
    }
}
