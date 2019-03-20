package com.jxq.mvp.account.model;

import android.os.Handler;
import android.util.Log;

import com.jxq.mvp.MyTaxiApplication;
import com.jxq.mvp.account.model.response.Account;
import com.jxq.mvp.account.model.response.LoginResponse;
import com.jxq.mvp.account.model.response.RegisterResponse;
import com.jxq.mvp.account.model.response.SmsCodeResponse;
import com.jxq.mvp.account.model.response.UserExistResponse;
import com.jxq.mvp.common.databus.RxBus;
import com.jxq.mvp.common.http.IHttpClient;
import com.jxq.mvp.common.http.IRequest;
import com.jxq.mvp.common.http.IResponse;
import com.jxq.mvp.common.http.api.API;
import com.jxq.mvp.common.http.biz.BaseBizResponse;
import com.jxq.mvp.common.http.impl.BaseRequest;
import com.jxq.mvp.common.http.impl.BaseResponse;
import com.jxq.mvp.common.storage.SharedPreferencesDao;
import com.jxq.mvp.common.util.DevUtil;
import com.jxq.mvp.common.util.LogUtil;
import com.google.gson.Gson;

import rx.functions.Func1;

/**
 * model层的实现
 * 所有实现类都要一个后缀impl:implement的意识
 * 从model到presenter有一个线程切换的过程：我们每个presenter都写一个handler来处理这一过程
 * 使用handler的时候，写法非常的繁琐
 */

public class AccountManagerImpl implements IAccountManager {

    private static final String TAG = "AccountManagerImpl";


    // 网络请求库
    private IHttpClient httpClient;
    // 数据存储
    private SharedPreferencesDao sharedPreferencesDao;
    // 发送消息 handler
    private Handler handler;

    public AccountManagerImpl(IHttpClient httpClient, SharedPreferencesDao sharedPreferencesDao) {
        this.httpClient = httpClient;
        this.sharedPreferencesDao = sharedPreferencesDao;
    }

    /**
     * handler是通过set接口获取的
     * @param handler
     */
    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * 获取验证码
     *
     * @param phone
     */
    @Override
    public void fetchSMSCode(final String phone) {
        /**
         * handler方法
         * 验证码逻辑copy过来的
         * 每次网络请求都会开启一个新的线程，这样线程开销会比较大的
         *
         * 从model到presenter有一个线程切换的过程，所以每个presenter都写一个handler来处理这个过程
         * 我们看到在使用hanger的时候写法非常的繁琐
         */
        new Thread(){
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = httpClient.get(request, false);
                Log.d(TAG, response.getData());
                LogUtil.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        handler.sendEmptyMessage(SMS_SEND_SUC);//使用handler发送消息
                    } else {
                        handler.sendEmptyMessage(SMS_SEND_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(SMS_SEND_FAIL);
                }
            }
        }.start();


        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = httpClient.get(request, false);
                Log.d(TAG, response.getData());
                SmsCodeResponse smsCodeResponse = new SmsCodeResponse();
                LogUtil.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        smsCodeResponse.setCode(SMS_SEND_SUC);
                    } else {
                        smsCodeResponse.setCode(SMS_SEND_FAIL);
                    }
                } else {
                    smsCodeResponse.setCode(SMS_SEND_FAIL);
                }
                return smsCodeResponse;
            }
        });
    }

    /**
     * 校验验证码
     *
     * @param phone
     * @param smsCode
     */
    @Override
    public void checkSmsCode(final String phone, final String smsCode) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("code", smsCode);
                IResponse response = httpClient.get(request, false);
                Log.d(TAG, response.getData());
                SmsCodeResponse smsCodeResponse = new SmsCodeResponse();

                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        smsCodeResponse.setCode(SMS_CHECK_SUC);
                    } else {
                        smsCodeResponse.setCode(SMS_CHECK_FAIL);
                    }
                } else {
                    smsCodeResponse.setCode(SMS_CHECK_FAIL);
                }
                return smsCodeResponse;
            }
        });
    }

    /**
     * 检查用户是否存在
     *
     * @param phone
     */
    @Override
    public void checkUserExist(final String phone) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.Config.getDomain() + API.CHECK_USER_EXIST;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = httpClient.get(request, false);
                Log.d(TAG, response.getData());
                UserExistResponse existResponse = new UserExistResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_USER_EXIST) {
                        existResponse.setCode(USER_EXIST);
                    } else if (bizRes.getCode() ==
                            BaseBizResponse.STATE_USER_NOT_EXIST) {
                        existResponse.setCode(USER_NOT_EXIST);
                    }
                } else {
                    existResponse.setCode(SERVER_FAIL);
                }
                return existResponse;
            }
        });
    }

    /**
     * 注册
     *
     * @param phone
     * @param password
     */
    @Override
    public void register(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.Config.getDomain() + API.REGISTER;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                request.setBody("uid", DevUtil.UUID(MyTaxiApplication.getInstance()));

                IResponse response = httpClient.post(request, false);
                Log.d(TAG, response.getData());

                RegisterResponse registerResponse = new RegisterResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(),
                                    BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        registerResponse.setCode(REGISTER_SUC);
                    } else {
                        registerResponse.setCode(SERVER_FAIL);
                    }
                } else {
                    registerResponse.setCode(SERVER_FAIL);
                }
                return registerResponse;
            }
        });
    }

    /**
     * 登录
     *
     * @param phone
     * @param password
     */
    @Override
    public void login(final String phone, final String password) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.Config.getDomain() + API.LOGIN;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);


                IResponse response = httpClient.post(request, false);
                Log.d(TAG, response.getData());

                LoginResponse bizRes = new LoginResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    bizRes = new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        Account account = bizRes.getData();
                        sharedPreferencesDao.save(SharedPreferencesDao.KEY_ACCOUNT, account);
                        // 通知 UI
                        bizRes.setCode(LOGIN_SUC);
                    } else if (bizRes.getCode() == BaseBizResponse.STATE_PW_ERR) {
                        bizRes.setCode(PW_ERROR);
                    } else {
                        bizRes.setCode(SERVER_FAIL);
                    }
                } else {
                    bizRes.setCode(SERVER_FAIL);
                }
                return bizRes;
            }
        });
    }

    /**
     * token 自动登录
     */
    @Override
    public void loginByToken() {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                // 获取本地登录信息
                Account account =
                        (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                                Account.class);


                // 登录是否过期：校验token
                boolean tokenValid = false;

                // 检查token是否过期

                if (account != null) {
                    if (account.getExpired() > System.currentTimeMillis()) {
                        // token 有效
                        tokenValid = true;
                    }
                }

                LoginResponse loginResponse = new LoginResponse();
                if (!tokenValid) {
                    loginResponse.setCode(TOKEN_INVALID);
                    return loginResponse;
                }


                // 请求网络，完成自动登录
                String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                IRequest request = new BaseRequest(url);
                request.setBody("token", account.getToken());
                IResponse response = httpClient.post(request, false);
                Log.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    loginResponse =
                            new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (loginResponse.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        account = loginResponse.getData();
                        // todo: 加密存储
                        sharedPreferencesDao.save(SharedPreferencesDao.KEY_ACCOUNT, account);
                        loginResponse.setCode(LOGIN_SUC);
                    } else
                    if (loginResponse.getCode() == BaseBizResponse.STATE_TOKEN_INVALID) {
                        loginResponse.setCode(TOKEN_INVALID);
                    }
                } else {
                    loginResponse.setCode(SERVER_FAIL);
                }
                return loginResponse;
            }
        });
    }

    @Override
    public boolean isLogin() {

        // 获取本地登录信息
        Account account =
                (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT,
                        Account.class);


        // 登录是否过期
        boolean tokenValid = false;

        // 检查token是否过期

        if (account != null) {
            if (account.getExpired() > System.currentTimeMillis()) {
                // token 有效
                tokenValid = true;
            }
        }
        return tokenValid;
    }
}
