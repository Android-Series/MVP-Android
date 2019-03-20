package com.jxq.mvp.main.model.response;

import com.jxq.mvp.common.http.biz.BaseBizResponse;
import com.jxq.mvp.common.lbs.LocationInfo;

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
