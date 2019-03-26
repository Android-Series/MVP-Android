package com.jxq.mvp.network.OkHttp.impl;

import com.jxq.mvp.network.OkHttp.IResponse;

/**
 * Created by liuguangli on 17/4/24.
 */

public class BaseResponse implements IResponse {

    public static final int STATE_UNKNOWN_ERROR = 100001;//把对应业务的错误码放到这里来
    public static final int STATE_OK = 200;

    // 网络层的状态码
    private int code;
    // 响应数据
    private String data;
    @Override
    public String getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(String data) {
        this.data = data;
    }
}
