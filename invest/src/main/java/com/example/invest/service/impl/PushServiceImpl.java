package com.example.invest.service.impl;

import com.example.invest.model.SystemConfig;
import com.example.invest.service.PushService;
import com.example.invest.util.SystemConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class PushServiceImpl implements PushService {

    @Resource
    private SystemConfigManager systemConfigManager;

    @Override
    public void pushMessage(String title, String content) {
        log.info("推送消息 - 标题: {}, 内容: {}", title, content);
        try {
            SystemConfig config = systemConfigManager.getConfig();
            if (config == null || config.getWechatPushToken() == null || config.getWechatPushToken().isEmpty()) {
                log.warn("微信推送配置未设置，跳过推送");
                return;
            }

            // 构建推送消息
            String message = String.format("### %s\n\n%s", title, content);
            
            // 发送微信推送
            String requestBody = String.format(
                "{\"appToken\":\"%s\",\"content\":\"%s\",\"summary\":\"%s\",\"contentType\":2,\"topicIds\":[\"%s\"]}",
                config.getWechatPushToken(),
                message,
                title,
                config.getWechatTemplateId()
            );
            
            cn.hutool.http.HttpRequest.post("https://wxpusher.zjiecode.com/api/send/message")
                    .body(requestBody)
                    .execute()
                    .body();
            
            log.info("消息推送成功");
        } catch (Exception e) {
            log.error("推送消息失败", e);
        }
    }

    @Override
    public void pushError(String title, String error) {
        log.error("推送错误 - 标题: {}, 错误: {}", title, error);
        try {
            SystemConfig config = systemConfigManager.getConfig();
            if (config == null || config.getWechatPushToken() == null || config.getWechatPushToken().isEmpty()) {
                log.warn("微信推送配置未设置，跳过错误推送");
                return;
            }

            // 构建错误消息
            String message = String.format("### 错误通知\n\n**%s**\n\n```\n%s\n```", title, error);
            
            // 发送微信推送
            String requestBody = String.format(
                "{\"appToken\":\"%s\",\"content\":\"%s\",\"summary\":\"错误通知: %s\",\"contentType\":2,\"topicIds\":[\"%s\"]}",
                config.getWechatPushToken(),
                message,
                title,
                config.getWechatTemplateId()
            );
            
            cn.hutool.http.HttpRequest.post("https://wxpusher.zjiecode.com/api/send/message")
                    .body(requestBody)
                    .execute()
                    .body();
            
            log.info("错误消息推送成功");
        } catch (Exception e) {
            log.error("推送错误消息失败", e);
        }
    }
} 