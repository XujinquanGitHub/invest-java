package com.example.invest.service;

import com.example.invest.model.WebMonitorConfig;
import com.example.invest.push.DingTalkPushUtil;
import com.example.invest.push.WeChatPushUtil;
import com.example.invest.util.ConfigManager;
import com.example.invest.util.CookieManager;
import com.example.invest.util.SystemConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.net.URL;
import javax.annotation.Resource;

@Slf4j
@Service
public class WebMonitorService {

    @Resource
    private ConfigManager configManager;
    
    @Resource
    private DingTalkPushUtil dingTalkPushUtil;
    
    @Resource
    private WeChatPushUtil weChatPushUtil;
    
    @Resource
    private SystemConfigManager systemConfigManager;

    @Resource
    private CookieManager cookieManager; // 注入 CookieManager
    
    // 存储每个配置的最后内容
    private final Map<String, String> lastContents = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        if (!systemConfigManager.getConfig().isEnablePageChangeMonitor()) {
            log.info("页面变化监控已禁用，跳过初始化。");
            return;
        }

        // 项目启动时加载所有配置
        List<WebMonitorConfig> configs = configManager.loadConfigs();
        for (WebMonitorConfig config : configs) {
            if (config.isEnabled()) {
                // 初始化时获取一次内容
                try {
                    String content = fetchContent(config);
                    lastContents.put(config.getId(), content);
                    log.info("初始化监控配置: {}, URL: {}", config.getName(), config.getUrl());
                } catch (Exception e) {
                    log.error("初始化监控配置失败: " + config.getName(), e);
                }
            }
        }
    }
    
    @Scheduled(fixedDelay = 60000) // 每分钟检查一次
    public void checkConfigs() {
        if (!systemConfigManager.getConfig().isEnablePageChangeMonitor()) {
            //log.info("页面变化监控已禁用，跳过检查。"); // 避免频繁日志
            return;
        }

        List<WebMonitorConfig> configs = configManager.loadConfigs();
        LocalTime now = LocalTime.now();
        
        for (WebMonitorConfig config : configs) {
            if (!config.isEnabled()) {
                continue;
            }
            
            // 检查是否在时间范围内
            if (config.isUseTimeRange()) {
                LocalTime startTime = config.getStartTime();
                LocalTime endTime = config.getEndTime();
                if (now.isBefore(startTime) || now.isAfter(endTime)) {
                    continue;
                }
            }
            
            // 检查是否到达检查间隔
            if (shouldCheck(config)) {
                try {
                    String newContent = fetchContent(config);
                    String lastContent = lastContents.get(config.getId());
                    
                    if (lastContent != null && !lastContent.equals(newContent)) {
                        // 内容发生变化，发送通知
                        String message = String.format("监控配置 [%s] 内容已更新\nURL: %s", 
                            config.getName(), config.getUrl());
                        
                        // 发送钉钉通知
                        dingTalkPushUtil.send("网页监控通知", message);
                        // 发送微信通知
                        weChatPushUtil.send("网页监控通知", message);
                        
                        log.info("检测到内容变化: {}", config.getName());
                    }
                    
                    // 更新最后内容
                    lastContents.put(config.getId(), newContent);
                } catch (Exception e) {
                    log.error("检查配置失败: " + config.getName(), e);
                }
            }
        }
    }
    
    private String fetchContent(WebMonitorConfig config) throws Exception {
        String urlString = config.getUrl();
        String domain = new URL(urlString).getHost();
        String cookies = cookieManager.getCookies(domain);

        org.jsoup.Connection connection = Jsoup.connect(urlString).timeout(10000);

        if (cookies != null && !cookies.isEmpty()) {
            // 将 Cookie 字符串解析为 Map<String, String>
            Map<String, String> cookieMap = new HashMap<>();
            Arrays.stream(cookies.split(";"))
                    .forEach(cookie -> {
                        String[] parts = cookie.split("=", 2);
                        if (parts.length == 2) {
                            cookieMap.put(parts[0].trim(), parts[1].trim());
                        }
                    });
            log.info("为域名 {} 添加 Cookie: {}", domain, cookieMap.keySet());
            connection.cookies(cookieMap);
        } else {
            log.info("域名 {} 无可用 Cookie，直接请求。");
        }

        Document doc = connection.get();
        return doc.select(config.getSelector()).text();
    }
    
    private boolean shouldCheck(WebMonitorConfig config) {
        // 这里可以根据配置的检查间隔（小时）来判断是否需要检查
        // 简单实现：根据当前时间的小时数来判断
        int currentHour = LocalTime.now().getHour();
        return currentHour % config.getInterval() == 0;
    }
} 