package com.jxq.mvp.network.OkHttp;

import com.jxq.mvp.network.OkHttp.IRequest;
import com.jxq.mvp.network.OkHttp.IResponse;

/**
 * HttpClient 抽象接口
 * Created by liuguangli on 17/4/24.
 */

public interface IHttpClient {
    IResponse get(IRequest request, boolean forceCache);
    IResponse post(IRequest request, boolean forceCache);
}
