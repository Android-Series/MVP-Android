package com.jxq.mvp.network.OkHttp.impl;

import com.jxq.mvp.network.OkHttp.IRequest;
import com.jxq.mvp.network.api.API;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现层
 * 封装参数的实现
 */

public class BaseRequest implements IRequest{
    private String method = POST; //默认post请求
    private String url; //请求行URL
    private Map<String, String> header;
    private Map<String, Object> body;

    public BaseRequest(String url) {
        /**
         *  公共参数及头部信息
         */
        this.url = url;
        header = new HashMap();
        body = new HashMap<>();
        header.put("X-Bmob-Application-Id", API.Config.getAppId());
        header.put("X-Bmob-REST-API-Key", API.Config.getAppKey());

    }

    @Override
    public void  setMethod(String method) {

       this.method = method;
    }
    public void setHeader(String key, String value) {
        header.put(key, value);
    }

    public void setBody(String key, String value) {
        body.put(key, value);
    }

    @Override
    public String getUrl() {
        if (GET.equals(method)) {
            // 组装 Get 请求参数
            for (String key : body.keySet()) { //从body里面把之前设置的键值对迭代出来

                url = url.replace("${" + key + "}", body.get(key).toString());//订了API的约定

            }
        }

        return url;
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }

    @Override
    public String getBody() {
        if (body != null) {
            // 组装 POST 方法请求参数
            return new Gson().toJson(this.body, HashMap.class);
        } else {
           return  "{}";
        }

    }
}
