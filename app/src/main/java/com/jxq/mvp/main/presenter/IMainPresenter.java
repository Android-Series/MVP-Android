package com.jxq.mvp.main.presenter;

import com.jxq.mvp.common.lbs.LocationInfo;

/**
 * Created by liuguangli on 17/5/14.
 */

public interface IMainPresenter {
    /**
     * 自动登陆的一个方法
     */
    void loginByToken();

    /**
     * 获取附近司机
     * @param latitude
     * @param longitude
     */
    void fetchNearDrivers(double latitude, double longitude);

    /**
     * 上报当前位置
     * @param locationInfo
     */
    void updateLocationToServer(LocationInfo locationInfo);


    /**
     * 呼叫司机
     * @param cost
     * @param key
     * @param mStartLocation
     * @param mEndLocation
     */
    void callDriver(String key, float cost, LocationInfo mStartLocation, LocationInfo mEndLocation);


    boolean isLogin();

    /**
     * 取消呼叫
     */
    void cancel();

    /**
     * 支付
     */
    void pay();

    /**
     *  获取正在处理中的订单
     */
    void getProcessingOrder();
}
