package com.example.invest.push;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WxPushUtil {
    @Value("${wx.push.token}")
    private String token;
    @Value("${wx.push.template.id}")
    private String templateId;

    public void send(String title, String content) {
        String url = "https://wxpusher.zjiecode.com/api/send/message";
        JSONObject body = new JSONObject();
        body.put("appToken", token);
        body.put("content", content);
        body.put("summary", title);
        body.put("contentType", 1);
        body.put("topicIds", new String[]{templateId});
        HttpRequest.post(url)
                .body(body.toJSONString())
                .execute()
                .body();
    }
} 