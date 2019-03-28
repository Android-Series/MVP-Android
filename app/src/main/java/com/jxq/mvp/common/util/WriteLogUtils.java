package com.jxq.mvp.common.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * create by jxq
 */
public class WriteLogUtils {
    /**
     * Log信息写入到手机SD卡
     */
    public static void StringBufferDemo(String text) throws IOException {
        String path="/sdcard/Download/PushLog.txt";
        File file = new File(path);
        if (!file.exists())
            file.createNewFile();
        FileOutputStream out = new FileOutputStream(file, true);
        StringBuffer sb = new StringBuffer();
        sb.append(text); // 直接在文件中追加文字
        out.write(sb.toString().getBytes("utf-8"));
        out.close();
    }

    public static void StringBufferDemo2(String text) throws IOException {
        String path="/sdcard/Download/SimulationPushLog.txt";
        File file = new File(path);
        if (!file.exists())
            file.createNewFile();
        FileOutputStream out = new FileOutputStream(file, true);
        StringBuffer sb = new StringBuffer();
        sb.append(text); // 直接在文件中追加文字
        out.write(sb.toString().getBytes("utf-8"));
        out.close();
    }

    /**
     * 删除手机SD卡之前的Log信息
     */
    public static void existsLogFileDelete(String url) throws IOException {
        File file = new File(url);
        if(file.exists())
            file.delete();
    }
    /**
     * 获取当前系统时间
     */
    public static String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();
        return "Test Time:"+sdf.format(date);
    }

    static int count=0;
    public static void startPushTest(){

        for(int i=0;i<5000;i++){

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");

            RequestBody body = RequestBody.create(mediaType, json());

            Request request = new Request.Builder()
                    .url("https://api.xiaomor.com/api/user/user/testPushMsg?key=3099B87833837758&ver=2")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String json = response.body().string();
                System.out.println("jxq:"+json);

                WriteLogUtils.StringBufferDemo2((count++)+"模拟推送:"+json +"\r\n");

                Thread.sleep(10000);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static String json(){
        String timestamp= System.currentTimeMillis() + "";

        JSONObject info=new JSONObject();
        JSONObject data=new JSONObject();
        String pushDatatValue1="{\"time\":\"12563563222\"},\"UUID\":\"xbjcb8e828uxsxsjd2u09snsjk2\"";
        String uuid = new UUID22().getUUID();
        String pushDatatValue ="{\"time\":"+ timestamp+ "},\"UUID\":"+ uuid;

        System.out.println("jxq----->timestamp: "+timestamp + "   uuid ： " + uuid);
        try {
            info.put("pushData",pushDatatValue);
            info.put("uid","430795193164234752");
            info.put("key","3099B87833837758");
            info.put("deviceId","1990fb4faebbd2ba4873ba1ce7b61331");
            info.put("pushType",1);
            data.put("data",info);
            System.out.println(data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

}
