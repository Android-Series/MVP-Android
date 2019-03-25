package com.jxq.mvp.main.model.response;


import com.jxq.mvp.common.lbs.LocationInfo;
import com.jxq.mvp.network.OkHttp.biz.BaseBizResponse;

import java.util.List;

/**
 * Created by liuguangli on 17/5/31.
 */

public class NearDriversResponse extends BaseBizResponse {

    List<LocationInfo> data;

    public List<LocationInfo> getData() {
        return data;
    }

    public void setData(List<LocationInfo> data) {
        this.data = data;
    }
}
