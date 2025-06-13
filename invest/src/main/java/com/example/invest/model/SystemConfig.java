package com.example.invest.model;

import lombok.Data;
import java.time.LocalTime;
import java.util.List;

@Data
public class SystemConfig {
    private boolean enableYunFeiMonitor;    // 是否开启云飞系统监控
    private boolean enablePageChangeMonitor; // 是否开启页面变化监控
    private String configPath;              // 配置文件路径

    // 微信推送配置
    private String wechatPushToken;         // 微信推送的token
    private String wechatTemplateId;        // 微信推送的模板ID
    private String wechatToUser;            // 微信推送的接收用户ID

    // 钉钉推送配置
    private String dingtalkWebhookUrl;      // 钉钉机器人的Webhook地址

    // 云飞监控配置
    private String yunFeiStrategyIds;       // 云飞策略ID，多个用逗号隔开
    private String yunFeiMonitorTimes;      // 云飞监控时间点，多个用逗号隔开 (例如: 08:00,09:05,14:55)
    private String yunFeiUsername;          // 云飞系统登录用户名
    private String yunFeiPassword;          // 云飞系统登录密码

    // 果仁网配置
    private List<GuorenConfig> guorenConfigs; // 果仁网多账号配置列表
} 