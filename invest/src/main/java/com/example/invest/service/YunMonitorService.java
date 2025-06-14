package com.example.invest.service;

import com.example.invest.model.YunMonitorConfig;
import com.example.invest.util.ConfigManager;
import com.example.invest.service.CookieManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class YunMonitorService {

    @Autowired
    private ConfigManager configManager;

    @Resource
    private CookieManager cookieManager;

    // 存储每个配置的最后检查时间
    private final Map<String, Long> lastCheckTimes = new ConcurrentHashMap<>();

    // 存储每个配置的最后内容
    private final Map<String, String> lastContents = new ConcurrentHashMap<>();

    // 存储每个配置的最后检查时间（手动执行）
    private final Map<String, Long> lastManualCheckTimes = new ConcurrentHashMap<>();

    // 存储每个配置的最后内容（手动执行）
    private final Map<String, String> lastManualContents = new ConcurrentHashMap<>();

    private final AtomicBoolean isMonitoring = new AtomicBoolean(false);

    public void startMonitoring() {
        isMonitoring.set(true);
        log.info("云飞监控已启动");
    }

    public void stopMonitoring() {
        isMonitoring.set(false);
        log.info("云飞监控已停止");
    }

    @Scheduled(fixedDelay = 60000) // 每分钟执行一次
    public void monitorYun() {
        if (!isMonitoring.get()) {
            return;
        }

        List<YunMonitorConfig> configs = configManager.loadYunMonitorConfigs();
        for (YunMonitorConfig config : configs) {
            try {
                // TODO: 实现云飞监控逻辑
                log.info("正在监控云飞: {}", config.getUrl());
            } catch (Exception e) {
                log.error("监控云飞时发生错误: {}", config.getUrl(), e);
            }
        }
    }

    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkConfigs() {
        List<YunMonitorConfig> configs = configManager.loadYunMonitorConfigs();
        for (YunMonitorConfig config : configs) {
            if (!config.isEnabled()) {
                continue;
            }

            // 检查是否在时间范围内
            if (config.isUseTimeRange()) {
                int currentHour = java.time.LocalTime.now().getHour();
                if (currentHour < config.getStartHour() || currentHour >= config.getEndHour()) {
                    continue;
                }
            }

            // 检查是否达到检查间隔
            long currentTime = System.currentTimeMillis();
            Long lastCheckTime = lastCheckTimes.get(config.getId());
            if (lastCheckTime != null && currentTime - lastCheckTime < config.getInterval() * 60 * 1000) {
                continue;
            }

            try {
                String content = fetchContent(config);
                String lastContent = lastContents.get(config.getId());

                if (lastContent != null && !lastContent.equals(content)) {
                    log.info("检测到内容变化 - 配置ID: {}, URL: {}", config.getId(), config.getUrl());
                    // TODO: 在这里添加内容变化后的处理逻辑，例如发送通知
                }

                lastContents.put(config.getId(), content);
                lastCheckTimes.put(config.getId(), currentTime);
            } catch (Exception e) {
                log.error("检查配置时出错 - 配置ID: {}, URL: {}, 错误: {}", config.getId(), config.getUrl(), e.getMessage());
            }
        }
    }

    // 手动执行监控
    public void executeMonitorManually(String configId) {
        List<YunMonitorConfig> configs = configManager.loadYunMonitorConfigs();
        for (YunMonitorConfig config : configs) {
            if (config.getId().equals(configId)) {
                try {
                    String content = fetchContent(config);
                    String lastContent = lastManualContents.get(config.getId());

                    if (lastContent != null && !lastContent.equals(content)) {
                        log.info("手动执行 - 检测到内容变化 - 配置ID: {}, URL: {}", config.getId(), config.getUrl());
                        // TODO: 在这里添加内容变化后的处理逻辑，例如发送通知
                    }

                    lastManualContents.put(config.getId(), content);
                    lastManualCheckTimes.put(config.getId(), System.currentTimeMillis());
                } catch (Exception e) {
                    log.error("手动执行 - 检查配置时出错 - 配置ID: {}, URL: {}, 错误: {}", config.getId(), config.getUrl(), e.getMessage());
                }
                break;
            }
        }
    }

    private String fetchContent(YunMonitorConfig config) throws IOException {
        String domain = extractDomain(config.getUrl());
        String cookies = cookieManager.getCookies(domain);

        Document doc = Jsoup.connect(config.getUrl())
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .timeout(10000)
                .get();

        if (cookies != null && !cookies.isEmpty()) {
            log.info("为域名 {} 添加 Cookie: {}", domain, cookies);
            doc = Jsoup.connect(config.getUrl())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Cookie", cookies)
                    .timeout(10000)
                    .get();
        }

        return doc.select(config.getSelector()).text();
    }

    private String extractDomain(String url) {
        try {
            java.net.URL urlObj = new java.net.URL(url);
            return urlObj.getHost();
        } catch (Exception e) {
            log.error("提取域名时出错 - URL: {}, 错误: {}", url, e.getMessage());
            return "";
        }
    }
} 