package com.jxq.mvp.account.model.response;


import com.jxq.mvp.network.OkHttp.biz.BaseBizResponse;

/**
 * Created by liuguangli on 17/5/6.
 */

public class LoginResponse extends BaseBizResponse {
    Account data;

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
