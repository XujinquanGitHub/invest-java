package com.example.invest.push;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.SystemConfig;
import com.example.invest.util.SystemConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class DingTalkPushUtil {

    @Resource
    private SystemConfigManager systemConfigManager;

    public void send(String title, String content) {
        log.info("尝试发送钉钉推送: 标题 - {}, 内容 - {}", title, content);

        // 获取配置
        SystemConfig config = systemConfigManager.getConfig();
        log.info("从 SystemConfigManager 获取到的完整配置: {}", config);

        String dingtalkWebhookUrl = config.getDingtalkWebhookUrl();
        log.info("从配置中提取的钉钉 Webhook URL: {}", dingtalkWebhookUrl);

        if (dingtalkWebhookUrl == null || dingtalkWebhookUrl.isEmpty()) {
            log.warn("钉钉推送配置不完整 (Webhook URL 为空)，跳过推送。");
            return;
        }
        title = "每日提醒" + title;
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