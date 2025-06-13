package com.example.invest.push;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.SystemConfig;
import com.example.invest.util.SystemConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DingTalkPushUtil {
    
    @Autowired
    private SystemConfigManager systemConfigManager;

    public void send(String title, String content) {
        SystemConfig config = systemConfigManager.getConfig();
        String dingtalkWebhookUrl = config.getDingtalkWebhookUrl();

        if (dingtalkWebhookUrl == null || dingtalkWebhookUrl.isEmpty()) {
            System.out.println("钉钉推送配置不完整，跳过推送。");
            return;
        }

        JSONObject data = new JSONObject();
        data.put("msgtype", "markdown");
        data.put("title", title);
        
        JSONObject markdown = new JSONObject()
                .fluentPut("title", title)
                .fluentPut("text", title + ":\n" + content);
        data.put("markdown", markdown);
        
        data.put("at", new JSONObject().fluentPut("isAtAll", true));
        
        String body = HttpRequest.post(dingtalkWebhookUrl)
                .body(JSON.toJSONString(data))
                .execute()
                .body();
        System.out.println("钉钉推送响应: " + body);
    }
} 