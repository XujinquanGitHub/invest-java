package com.example.invest.push;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.SystemConfig;
import com.example.invest.util.SystemConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeChatPushUtil {
    
    @Autowired
    private SystemConfigManager systemConfigManager;

    public void send(String title, String content) {
        SystemConfig config = systemConfigManager.getConfig();
        String token = config.getWechatPushToken();
        String templateId = config.getWechatTemplateId();
        String toUser = config.getWechatToUser();

        if (token == null || token.isEmpty() || templateId == null || templateId.isEmpty() || toUser == null || toUser.isEmpty()) {
            System.out.println("微信推送配置不完整，跳过推送。");
            return;
        }

        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token;
        
        JSONObject body = new JSONObject();
        body.put("touser", toUser);
        body.put("template_id", templateId);
        
        JSONObject data = new JSONObject();
        
        // 标题
        JSONObject titleObj = new JSONObject();
        titleObj.put("value", title);
        titleObj.put("color", "#173177");
        data.put("title", titleObj);
        
        // 内容
        JSONObject contentObj = new JSONObject();
        contentObj.put("value", content);
        contentObj.put("color", "#173177");
        data.put("content", contentObj);
        
        body.put("data", data);
        
        String response = HttpRequest.post(url)
                .body(body.toJSONString())
                .execute()
                .body();
        System.out.println("微信推送响应: " + response);
    }
} 