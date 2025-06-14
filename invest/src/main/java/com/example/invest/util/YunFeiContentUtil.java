package com.example.invest.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.invest.model.SystemConfig;
import com.example.invest.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;

@Slf4j
@Component
public class YunFeiContentUtil {
    
    @Resource
    private YunFeiLoginUtil yunFeiLoginUtil;
    
    @Resource
    private PushService pushService;
    
    @Resource
    private SystemConfigManager systemConfigManager;
    
    private static final String BASE_URL = "http://www.yunfei.com";
    
    /**
     * 获取策略详情内容
     * @param id 策略ID
     */
    public void getStrategyDetail(String id) {
        String url = BASE_URL + "/F2/c_detail.aspx?id=" + id;
        
        // 获取已保存的Cookie
        String cookies = yunFeiLoginUtil.getCookies();
        if (cookies == null) {
            pushService.pushError("错误提示", "未找到登录Cookie，请先登录");
            return;
        }
        
        try {
            // 发送请求获取页面内容
            HttpResponse response = HttpRequest.get(url)
                    .header("Cookie", cookies)
                    .execute();
            
            // 解析HTML
            Document doc = Jsoup.parse(response.body());
            
            // 获取class="content"下的第一个table
            Element content = doc.selectFirst(".content");
            if (content != null) {
                Element table = content.selectFirst("table");
                if (table != null) {
                    // 发送推送
                    pushService.pushMessage("策略详情", table.text());
                } else {
                    pushService.pushMessage("提示", "未找到策略内容");
                }
            } else {
                pushService.pushMessage("提示", "未找到内容区域");
            }
        } catch (Exception e) {
            pushService.pushError("错误提示", "获取策略详情失败：" + e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 60000) // 每分钟检查一次
    public void scheduledYunFeiMonitorTask() {
        SystemConfig config = systemConfigManager.getConfig();
        if (!config.isEnableYunFeiMonitor()) {
            //log.info("云飞系统监控已禁用，跳过检查。"); // 避免频繁日志
            return;
        }

        String monitorTimesStr = config.getYunFeiMonitorTimes();
        if (monitorTimesStr == null || monitorTimesStr.isEmpty()) {
            log.warn("云飞监控时间点未配置，跳过检查。");
            return;
        }

        LocalTime now = LocalTime.now();
        boolean shouldExecute = Arrays.stream(monitorTimesStr.split(","))
                .anyMatch(timeStr -> {
                    try {
                        LocalTime scheduledTime = LocalTime.parse(timeStr.trim());
                        // 检查当前时间是否在计划时间的一分钟内
                        return now.isAfter(scheduledTime.minusMinutes(1)) && now.isBefore(scheduledTime.plusMinutes(1));
                    } catch (Exception e) {
                        log.error("解析云飞监控时间点失败: {}", timeStr, e);
                        return false;
                    }
                });

        if (shouldExecute) {
            log.info("执行云飞系统监控任务...");
            String strategyIdsStr = config.getYunFeiStrategyIds();
            if (strategyIdsStr != null && !strategyIdsStr.isEmpty()) {
                List<String> strategyIds = Arrays.asList(strategyIdsStr.split(","));
                for (String id : strategyIds) {
                    try {
                        log.info("获取云飞策略详情，ID: {}", id);
                        getStrategyDetail(id.trim());
                        Thread.sleep(2000); // 增加延迟，避免请求过快
                    } catch (Exception e) {
                        log.error("获取云飞策略详情失败，ID: {}", id, e);
                    }
                }
            } else {
                log.warn("云飞策略ID未配置。");
            }
        }
    }
} 