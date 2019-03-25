package com.jxq.mvp.main.model;

import com.jxq.mvp.TaxiApplication;
import com.jxq.mvp.account.model.response.Account;
import com.jxq.mvp.common.databus.RxBus;
import com.jxq.mvp.common.http.IHttpClient;
import com.jxq.mvp.common.http.IRequest;
import com.jxq.mvp.common.http.IResponse;
import com.jxq.mvp.common.http.api.API;
import com.jxq.mvp.common.http.biz.BaseBizResponse;
import com.jxq.mvp.common.http.impl.BaseRequest;
import com.jxq.mvp.common.lbs.LocationInfo;
import com.jxq.mvp.common.storage.SharedPreferencesDao;
import com.jxq.mvp.common.util.LogUtil;
import com.jxq.mvp.main.model.response.NearDriversResponse;
import com.jxq.mvp.main.model.response.OrderStateOptResponse;
import com.google.gson.Gson;

import rx.functions.Func1;

/**
 * Created by liuguangli on 17/5/31.
 */

public class MainMangerImpl implements IMainManager{

    private static final String TAG = "MainMangerImpl";

    IHttpClient mHttpClient; //要做网络请求了

    public MainMangerImpl(IHttpClient mHttpClient) {
        this.mHttpClient = mHttpClient;
    }

    @Override
    public void fetchNearDrivers(final double latitude, final double longitude) {

        RxBus.getInstance().chainProcess(new Func1() {
            /**
             * 这里做网络请求的处理
             * @param o
             * @return
             */
            @Override
            public Object call(Object o) {

                IRequest request = new BaseRequest(API.Config.getDomain() + API.GET_NEAR_DRIVERS);

                request.setBody("latitude", new Double(latitude).toString() );
                request.setBody("longitude", new Double(longitude).toString() );
                IResponse response = mHttpClient.get(request, false);

                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    try {
                        NearDriversResponse nearDriversResponse = new Gson().fromJson(response.getData(), NearDriversResponse.class);
                        return nearDriversResponse; //正常返回；写好之后，要在presenter层接收一下，使用注解去接收一下，所以我们要去注册
                    } catch (Exception e) {
                        //做try catch是因为，不一定相信服务器返回的就是一个合法的字符串，返回空也就是我们的业务层不需要处理异常
                        return null;
                    }
                }
                return null;
            }
        });
    }

    /**
     * 最终由我们的IMainManger的实现类：MainMangerImpl来完成最终的逻辑
     * @param locationInfo
     */
    @Override
    public void updateLocationToServer(final LocationInfo locationInfo) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.Config.getDomain() + API.UPLOAD_LOCATION);
                request.setBody("latitude", new Double(locationInfo.getLatitude()).toString() );
                request.setBody("longitude", new Double(locationInfo.getLongitude()).toString() );
                request.setBody("key",locationInfo.getKey());
                request.setBody("rotation", new Float(locationInfo.getRotation()).toString() );
                IResponse response = mHttpClient.post(request, false);
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    LogUtil.d(TAG, "位置上报成功");
                } else {
                    LogUtil.d(TAG, "位置上报失败");
                }
                return null;
            }
        });
    }
    /**
     * 呼叫司机
     * @param key
     * @param startLocation
     * @param endLocation
     */
    @Override
    public void callDriver(final String key, final float cost, final LocationInfo startLocation, final LocationInfo endLocation) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                /**
                 *  获取 uid,phone
                  */
                SharedPreferencesDao sharedPreferencesDao = new SharedPreferencesDao(TaxiApplication.getInstance(),
                                SharedPreferencesDao.FILE_ACCOUNT);
                Account account = (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT, Account.class);
                String uid = account.getUid();
                String phone = account.getAccount();
                IRequest request = new BaseRequest(API.Config.getDomain() + API.CALL_DRIVER);
                request.setBody("key", key);
                request.setBody("uid",uid);
                request.setBody("phone", phone);
                request.setBody("startLatitude", new Double(startLocation.getLatitude()).toString() );
                request.setBody("startLongitude", new Double(startLocation.getLongitude()).toString() );
                request.setBody("endLatitude", new Double(endLocation.getLatitude()).toString() );
                request.setBody("endLongitude", new Double(endLocation.getLongitude()).toString() );
                request.setBody("cost", new Float(cost).toString());

                IResponse response = mHttpClient.post(request, false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    // 解析订单信息
                    orderStateOptResponse = new Gson().fromJson(response.getData(), OrderStateOptResponse.class);
                }
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_CREATE);
                LogUtil.d(TAG, "call driver: " + response.getData());

                return orderStateOptResponse;
            }
        });
    }
    /**
     * 取消订单
     * @param orderId
     */
    @Override
    public void cancelOrder(final String orderId) {

        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                IRequest request = new BaseRequest(API.Config.getDomain() + API.CANCEL_ORDER);

                request.setBody("id", orderId);

                IResponse response = mHttpClient.post(request, false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_CANCEL);

                LogUtil.d(TAG, "cancel order: " + response.getData());
                return orderStateOptResponse;
            }
        });
    }

    /**
     * 支付
     * @param orderId
     */
    @Override
    public void pay(final String orderId) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {

                IRequest request = new BaseRequest(API.Config.getDomain() + API.PAY);

                request.setBody("id", orderId);
                IResponse response = mHttpClient.post(request, false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.PAY);

                LogUtil.d(TAG, "pay order: " + response.getData());
                return orderStateOptResponse;
            }
        });
    }

    /**
     *  获取进行中的订单
     */

    @Override
    public void getProcessingOrder() {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                /**
                 * 获取 uid
                 */
                SharedPreferencesDao sharedPreferencesDao = new SharedPreferencesDao(TaxiApplication.getInstance(),
                                SharedPreferencesDao.FILE_ACCOUNT);
                Account account = (Account) sharedPreferencesDao.get(SharedPreferencesDao.KEY_ACCOUNT, Account.class);
                String uid = account.getUid();
                IRequest request = new BaseRequest(API.Config.getDomain() + API.GET_PROCESSING_ORDER);
                request.setBody("uid", uid);

                IResponse response = mHttpClient.get(request, false);
                LogUtil.d(TAG, "getProcessingOrder order: " + response.getData());
                if (response.getCode() == BaseBizResponse.STATE_OK) {
                    /**
                     * 解析订单数据，封装到 OrderStateOptResponse
                     */
                    OrderStateOptResponse orderStateOptResponse = new Gson().fromJson(response.getData(), OrderStateOptResponse.class);
                    if (orderStateOptResponse.getCode() == BaseBizResponse.STATE_OK) {
                        orderStateOptResponse.setState(orderStateOptResponse.getData().getState());
                        LogUtil.d(TAG, "getProcessingOrder order state=" + orderStateOptResponse.getState());
                        return orderStateOptResponse;
                    }
                }
                return null;
            }
        });
    }
}
