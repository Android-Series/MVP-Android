package com.jxq.mvp.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.jxq.mvp.account.model.IAccountManager;
import com.jxq.mvp.account.model.response.SmsCodeResponse;
import com.jxq.mvp.account.model.response.UserExistResponse;
import com.jxq.mvp.account.view.ISmsCodeDialogView;
import com.jxq.mvp.account.view.SmsCodeDialog;
import com.jxq.mvp.common.databus.RegisterBus;

import java.lang.ref.WeakReference;

/**
 * 验证presenter码输入框的实现类:实现UI交互逻辑
 * 不要presenter依赖具体的实现
 */

public class SmsCodeDialogPresenterImpl implements ISmsCodeDialogPresenter {

    private ISmsCodeDialogView view;
    private IAccountManager accountManager;

    /**
     * 接受消息并处理:使用一个静态内部类来定义
     */
    private static class MyHandler extends Handler{
        //使用弱引用，来引用我们上下文对象，这样做是为了防止我上下文对象内存泄漏，
        // 软引用和弱引用一样，不过更推荐使用弱引用，弱引用回收效率会更高一些
        WeakReference<SmsCodeDialogPresenterImpl> refContext;
        public MyHandler(SmsCodeDialogPresenterImpl context){
            refContext=new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            SmsCodeDialogPresenterImpl presenter=refContext.get();
            switch (msg.what){
                case IAccountManager.SMS_SEND_SUC:
                    presenter.view.showCountDownTimer();
                    break;
                case IAccountManager.SMS_SEND_FAIL:
                    presenter.view.showError(IAccountManager.SMS_SEND_FAIL,"");
                    break;
                case IAccountManager.SMS_CHECK_SUC:
                    presenter.view.showSmsCodeCheckState(true);
                    break;
                case IAccountManager.SMS_CHECK_FAIL:
                    presenter.view.showError(IAccountManager.SMS_CHECK_FAIL,"");
                    break;
                case IAccountManager.USER_EXIST: //提示用户是否存在
                    presenter.view.showUserExist(true);
                    break;
                case IAccountManager.USER_NOT_EXIST:
                    presenter.view.showUserExist(false);
                    break;

            }
        }
    }


    /**
     * presenter向上依赖，向下依赖，是依赖的接口，而不是依赖具体的实现，所以这里传过来的都是接口类型；
     * 从构造函数的参数就能看出
     * @param view
     * @param accountManager
     */
    public SmsCodeDialogPresenterImpl(ISmsCodeDialogView view, IAccountManager accountManager) {

        this.view = view;
        this.accountManager = accountManager;
        //accountManager.setHandler(new MyHandler(this));
    }

    @RegisterBus
    public void onSmsCodeResponse(SmsCodeResponse smsCodeResponse) {
        switch (smsCodeResponse.getCode()) {
            case IAccountManager.SMS_SEND_SUC:
                view.showCountDownTimer();
                break;
            case IAccountManager.SMS_SEND_FAIL:
                view.showError(IAccountManager.SMS_SEND_FAIL, "");
                break;
            case IAccountManager.SMS_CHECK_SUC:
                view.showSmsCodeCheckState(true);

                break;
            case IAccountManager.SMS_CHECK_FAIL:
                view.showError(IAccountManager.SMS_CHECK_FAIL, "");
                break;
            case IAccountManager.SERVER_FAIL:
                view.showError(IAccountManager.SERVER_FAIL, "");
                break;
        }
    }

    @RegisterBus
    public void onSmsCodeResponse(UserExistResponse userExistResponse) {
        switch (userExistResponse.getCode()) {

            case IAccountManager.USER_EXIST:
                view.showUserExist(true);
                break;
            case IAccountManager.USER_NOT_EXIST:
               view.showUserExist(false);
                break;
            case IAccountManager.SERVER_FAIL:
                view.showError(IAccountManager.SERVER_FAIL, "");
                break;

        }
    }

    /**
     * 获取验证码
     * @param phone
     */
    @Override
    public void requestSendSmsCode(String phone) {
        accountManager.fetchSMSCode(phone);//到这里就完成了指令传递的逻辑了：就是调用我们model层的方法
    }

    /**
     * 验证码这验证码
     * @param phone
     * @param smsCode
     */
    @Override
    public void requestCheckSmsCode(String phone, String smsCode) {

        accountManager.checkSmsCode(phone, smsCode);
    }

    /**
     * 检查用户是否存在
     * @param phone
     */
    @Override
    public void requestCheckUserExist(String phone) {

        accountManager.checkUserExist(phone);
    }
}
