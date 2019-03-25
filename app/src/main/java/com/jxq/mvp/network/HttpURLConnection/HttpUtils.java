package com.jxq.mvp.network.HttpURLConnection;

import android.os.Handler;
import android.os.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static void get(final String url, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn;
                InputStream is;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");
                    is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    StringBuilder result = new StringBuilder();//字符串拼接，StringBuilder比字符串相加的形式高效很多
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Message msg = new Message();//获取到json数据后，通过Handler通知主线程
                    msg.obj = result.toString();
                    //通过message通知主线程（也就是UI线程），我的json数据收到了，接下来就由你的Handler来处理
                    handler.sendMessage(msg); //需要重写Handler中的HandleMessage()方法：接收、处理发送过来的数据
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void post() {

    }

}
