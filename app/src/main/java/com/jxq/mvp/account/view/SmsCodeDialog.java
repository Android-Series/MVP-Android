package com.jxq.mvp.account.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dalimao.corelibrary.VerificationCodeInput;
import com.jxq.mvp.MyTaxiApplication;
import com.jxq.mvp.R;
import com.jxq.mvp.account.model.AccountManagerImpl;
import com.jxq.mvp.account.model.IAccountManager;
import com.jxq.mvp.account.presenter.ISmsCodeDialogPresenter;
import com.jxq.mvp.account.presenter.SmsCodeDialogPresenterImpl;
import com.jxq.mvp.common.databus.RxBus;
import com.jxq.mvp.common.http.IHttpClient;
import com.jxq.mvp.common.http.IRequest;
import com.jxq.mvp.common.http.IResponse;
import com.jxq.mvp.common.http.api.API;
import com.jxq.mvp.common.http.biz.BaseBizResponse;
import com.jxq.mvp.common.http.impl.BaseRequest;
import com.jxq.mvp.common.http.impl.BaseResponse;
import com.jxq.mvp.common.http.impl.OkHttpClientImpl;
import com.jxq.mvp.common.storage.SharedPreferencesDao;
import com.jxq.mvp.common.util.ToastUtil;
import com.jxq.mvp.main.view.MainActivity;
import com.google.gson.Gson;

import java.lang.ref.SoftReference;


/**
 * view层：把它变成一个只负责渲染的view，不处理复杂逻辑，变成一个被动视图
 * 验证码输入层怎么去把它清理一下
 */

public class SmsCodeDialog extends Dialog  implements  ISmsCodeDialogView{
    private static final String TAG = "SmsCodeDialog";

    private String mPhone;
    private Button mResentBtn;
    private VerificationCodeInput mVerificationInput;
    private View mLoading;
    private View mErrorView;
    private TextView mPhoneTv;
    private ISmsCodeDialogPresenter mPresenter;
    private MainActivity mainActivity;

    /**
     *  验证码倒计时
     */
    private CountDownTimer mCountDownTimer = new CountDownTimer(60000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            mResentBtn.setEnabled(false);
            mResentBtn.setText(String.format(getContext()
                    .getString(R.string.after_time_resend,
                            millisUntilFinished/1000)));
        }

        @Override
        public void onFinish() {
            mResentBtn.setEnabled(true);
            mResentBtn.setText(getContext().getString(R.string.resend));
            cancel();
        }
    };



    public SmsCodeDialog(MainActivity context, String phone) {
        this(context, R.style.Dialog);
        // 上一个界面传来的手机号
        this.mPhone = phone;
        IHttpClient httpClient = new OkHttpClientImpl();
        SharedPreferencesDao dao =
                new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                SharedPreferencesDao.FILE_ACCOUNT);
        IAccountManager iAccountManager = new AccountManagerImpl(httpClient, dao);
        mPresenter = new SmsCodeDialogPresenterImpl(this, iAccountManager);
       this.mainActivity = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_smscode_input, null);
        setContentView(root);
        mPhoneTv = (TextView) findViewById(R.id.phone);
        String template = getContext().getString(R.string.sending);
        mPhoneTv.setText(String.format(template, mPhone));
        mResentBtn = (Button) findViewById(R.id.btn_resend);
        mVerificationInput = (VerificationCodeInput) findViewById(R.id.verificationCodeInput);
        mLoading = findViewById(R.id.loading);
        mErrorView = findViewById(R.id.error);
        mErrorView.setVisibility(View.GONE);
        initListeners();
        requestSendSmsCode();//请求发送验证码

        // 注册 Presenter
        RxBus.getInstance().register(mPresenter);
    }
    @Override
    public void dismiss() {
        super.dismiss();
        // 注销 Presenter
        RxBus.getInstance().unRegister(mPresenter);
    }

    /**
     * 请求下发验证码
     */
    private void requestSendSmsCode() {
        mPresenter.requestSendSmsCode(mPhone);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCountDownTimer.cancel();

    }

    public SmsCodeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SmsCodeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    private void initListeners() {

        //  关闭按钮组册监听器
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 重发验证码按钮注册监听器
        mResentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend();
            }
        });

        // 验证码输入完成监听器
        mVerificationInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String code) {

                commit(code);
            }
        });
    }

    /**
     * 提交验证码：调用我们的presenter去请求的
     * @param code
     */
    private void commit(final String code) {
        mPresenter.requestCheckSmsCode(mPhone, code);
    }

    private void resend() {

        String template = getContext().getString(R.string.sending);
        mPhoneTv.setText(String.format(template, mPhone));

    }


    @Override
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 显示错误
     * 1、到这里，我们完成来AccountManager的实现，
     * 2、以及我们验证码输入框对应的presenter层的实现，
     * 3、我们验证码输入框view层的实现。
     * 也就是说我们完成了MVP一个完整的分层实现
     * @param code
     * @param msg
     */
    @Override
    public void showError(int code, String msg) {
        mLoading.setVisibility(View.GONE);//loading隐藏掉
        switch (code) {
            case IAccountManager.SMS_SEND_FAIL:
               ToastUtil.show(getContext(),
                       getContext().getString(R.string.sms_send_fail));
                break;
            case IAccountManager.SMS_CHECK_FAIL:
                // 提示验证码错误
                mErrorView.setVisibility(View.VISIBLE);
                mVerificationInput.setEnabled(true);

                break;
            case IAccountManager.SERVER_FAIL:
                ToastUtil.show(getContext(),
                        getContext().getString(R.string.error_server));
                break;
        }

    }

    /**
     * 下发验证码成功的一个回调
     */
    @Override
    public void showCountDownTimer() {
        mPhoneTv.setText(String.format(getContext().getString(R.string.sms_code_send_phone), mPhone));
        mCountDownTimer.start();
        mResentBtn.setEnabled(false);//把重新发送验证码的按钮灰掉

    }


    /**
     * 检测验证码的检验结果
     * @param suc
     */
    @Override
    public void showSmsCodeCheckState(boolean suc) {
        if (!suc) {
            //提示验证码错误
            mErrorView.setVisibility(View.VISIBLE);
            mVerificationInput.setEnabled(true);
            mLoading.setVisibility(View.GONE);
        } else {

            mErrorView.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
            mPresenter.requestCheckUserExist(mPhone);//检测用户是否存在
        }
    }

    /**
     * 显示用户是否存在
     * @param exist
     */
    @Override
    public void showUserExist(boolean exist) {
        mLoading.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        dismiss();
        if (!exist) {
            // 用户不存在,进入注册
            CreatePasswordDialog dialog =
                    new CreatePasswordDialog(getContext(), mPhone);
            dialog.show();
            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    SmsCodeDialog.this.dismiss();
                }
            });

        } else {
            // 用户存在 ，进入登录
            LoginDialog dialog = new LoginDialog(mainActivity, mPhone);
            dialog.show();
            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    SmsCodeDialog.this.dismiss();
                }
            });
        }
    }
}
