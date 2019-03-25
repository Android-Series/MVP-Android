package com.jxq.mvp;

import com.jxq.mvp.network.OkHttp.IHttpClient;
import com.jxq.mvp.network.OkHttp.IRequest;
import com.jxq.mvp.network.OkHttp.IResponse;
import com.jxq.mvp.network.OkHttp.impl.BaseRequest;
import com.jxq.mvp.network.OkHttp.impl.OkHttpClientImpl;
import com.jxq.mvp.main.model.MainMangerImpl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test01(){
        MainMangerImpl mainManger=new MainMangerImpl(new OkHttpClientImpl());
        mainManger.cancelOrder("12345");
    }

    @Test
    public void test02(){
        IRequest request=new BaseRequest("http://httpbin.org/get?id=id");
        IHttpClient httpClient=new OkHttpClientImpl();
        IResponse response=httpClient.get(request,false);
        System.out.println(response.getData());
    }
}