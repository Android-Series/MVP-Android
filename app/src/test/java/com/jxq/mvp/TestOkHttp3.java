package com.jxq.mvp;

/**
 * Created by liuguangli on 17/4/24.
 */
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestOkHttp3 {
    /**
     * 测试 OkHttp Get 方法
     */
    @Test
    public void testGet() {
        // 创建 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient();
        // 创建 Request 对象，Request它使用建造者模式来封装的
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 OkHttp Post 方法
     */
    @Test
    public void testPost() {
        // 创建 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient();
        // 创建 Request 对象
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8"); //请求格式，定义的格式是json
        // 创建请求体
        RequestBody body = RequestBody.create(mediaType, "{\"name\": \"hello world !\"}");
        Request request = new Request.Builder()
                .url("http://httpbin.org/post")// 请求行
                //.header(); // 请求头
                .post(body) // 请求体
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  测试拦截器
     */
    @Test
    public void testInterceptor() {
        //  定义拦截器，实现Interceptor接口
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                long start = System.currentTimeMillis(); //起始时间
                Request request  = chain.request(); //截获我们的请求
                Response response = chain.proceed(request); //request对象我们要继续往下传递
                long end = System.currentTimeMillis(); //请求结束的时间
                System.out.println("interceptor: cost time = " + (end - start));
                return response; //返回到业务层去
            }
        };
        // 创建 OkHttpClient 对象，OkHttpClient对象的另一种创建方式，可以指定需要的属性
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor) //添加拦截器，拦截器是可以添加多个的，添加拦截器之后，所有client对象的请求都会被拦截器拦截到
                .build();
        // 创建 Request 对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  测试缓存
     */
    @Test
    public void testCache() {
        // 创建缓存对象
        Cache cache = new Cache(new File("cache.cache"), 1024 * 1024);//两个参数：指定文件目录和缓存大小
            // 创建 OkHttpClient 对象，缓存是OkHttpClient的一个属性：所以使用建造者模式这个方法来去创建
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            // 创建 Request 对象
            Request request = new Request.Builder()
                    .url("http://httpbin.org/get?id=id")
                    .cacheControl(CacheControl.FORCE_NETWORK)//强制从网络取数据，这个特性作用很大，在有网络的时候在网络上取，在没有网络的时候从缓存中去取
                    .build();
            // OkHttpClient 执行 Request
            try {
                Response response = client.newCall(request).execute();
                Response responseCache = response.cacheResponse();
                Response responseNet = response.networkResponse();
                if (responseCache != null) {
                    // 从缓存响应
                    System.out.println("response from cache");
                }
                if (responseNet != null) {
                    // 从缓存响应
                    System.out.println("response from net");
                }

                System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
