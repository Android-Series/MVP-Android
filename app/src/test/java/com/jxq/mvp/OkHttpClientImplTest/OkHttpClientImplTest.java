package com.jxq.mvp.OkHttpClientImplTest;
import com.jxq.mvp.network.OkHttp.IHttpClient;
import com.jxq.mvp.network.OkHttp.IRequest;
import com.jxq.mvp.network.OkHttp.IResponse;
import com.jxq.mvp.network.api.API;
import com.jxq.mvp.network.OkHttp.impl.BaseRequest;
import com.jxq.mvp.network.OkHttp.impl.OkHttpClientImpl;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by liuguangli on 17/4/29.
 */
public class OkHttpClientImplTest {
    IHttpClient httpClient;
    @Before
    public void setUp() throws Exception {
        httpClient = new OkHttpClientImpl();
        API.Config.setDebug(true);
    }

    @Test
    public void get() throws Exception {
        // 设置 request 参数
        String url = API.Config.getDomain() + API.TEST_GET;
        IRequest request = new BaseRequest(url);
        request.setHeader("testHeader", "test header");
        request.setBody("uid", "123456");
        IResponse response = httpClient.get(request, false);
        System.out.println("stateCode = " + response.getCode());
        System.out.println("body =" + response.getData());
    }

    @Test
    public void post() throws Exception {

        // 设置 request 参数
        String url = API.Config.getDomain() + API.TEST_POST;
        IRequest request = new BaseRequest(url);
        request.setHeader("testHeader", "test header");
        request.setBody("uid", "123456");
        IResponse response = httpClient.post(request, false);
        System.out.println("stateCode = " + response.getCode());
        System.out.println("body =" + response.getData());
    }

}