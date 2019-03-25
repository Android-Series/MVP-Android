package com.jxq.mvp.network.OkHttp;

import java.util.Map;

/**
 * 1、定义请求的数据和行为是怎样去封装的
 * 2、定义请求数据的封装方式
 * 3、抽象层
 */
public interface IRequest {
    /**
     * 首先指定请求的方式
     */
    public static final String POST = "POST";
    public static final String GET = "GET";

    /**
     * 指定请求方式
      */
    void setMethod(String method);

    /**
     * 指定请求头部
     * @param key
     * @param value
     */
    void setHeader(String key, String value);

    /**
     *  指定请求参数：采用键-值对去对应的
     * @param key
     * @param value
     */
    void setBody(String key, String value);

    /**
     * 提供给执行库请求行URL
     * @return
     */
    String getUrl();

    /**
     * 提供给执行库请求头部
     * @return
     */
    Map<String, String> getHeader();

    /**
     * 提供给执行库请求参数
     * @return
     */
    Object getBody();

}
