package com.demo.ganktest.util;

import android.text.TextUtils;

import com.demo.ganktest.db.Mito;
import com.demo.ganktest.gson.Message;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    /**
     * 解析和处理服务器返回的随机20条数据,解析成Message实体类
     */
    public static List<Message> handleMessageResponse(String response) {
        List<Message> messageList = new ArrayList<>();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String messageContent = jsonArray.getJSONObject(i).toString();
                    Message message = new Gson().fromJson(messageContent, Message.class);
                    messageList.add(message);
                }
                return messageList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

//    /**
//     * 通过解析Message，从中拿到图片Url部分
//     */
//    public static List<String> handleMitoResponse(List<Message> messageList){
//        List<String> mitoUrlList=new ArrayList<>();
//        if(messageList!=null) {
//            for (int i = 0; i < messageList.size(); i++) {
//                Message message = messageList.get(i);
//                mitoUrlList.add(message.url);
//            }
//            return mitoUrlList;
//        }
//        return null;
//    }

    /**
     * 通过解析本地Url列表得到Mito实体类
     */
    public static Mito[] handleMitoResponse(List<Message> messageList) {
        Mito[] mitos = new Mito[20];
        if (messageList != null) {
            for (int i = 0; i < mitos.length; i++) {
                Message message = messageList.get(i);
                mitos[i]=new Mito();
                mitos[i].setName(message.desc);
                mitos[i].setImageUrl(message.url);
            }
            return mitos;
        }
        return null;
    }
}
