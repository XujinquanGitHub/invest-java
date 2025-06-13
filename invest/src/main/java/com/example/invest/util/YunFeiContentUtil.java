package com.example.invest.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.invest.model.SystemConfig;
import com.example.invest.service.PushService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YunFeiContentUtil {
    
    @Autowired
    private YunFeiLoginUtil yunFeiLoginUtil;
    
    @Autowired
    private PushService pushService;
    
    @Autowired
    private SystemConfigManager systemConfigManager;
    
    private static final String BASE_URL = "https://www.ycyflh.com";
    
    /**
     * 获取策略详情内容
     * @param id 策略ID
     */
    public void getStrategyDetail(String id) {
        String url = BASE_URL + "/F2/c_detail.aspx?id=" + id;
        
        // 获取已保存的Cookie
        String cookies = yunFeiLoginUtil.getCookies();
        if (cookies == null) {
            pushService.send("错误提示", "未找到登录Cookie，请先登录");
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
                    pushService.send("策略详情", table.text());
                } else {
                    pushService.send("提示", "未找到策略内容");
                }
            } else {
                pushService.send("提示", "未找到内容区域");
            }
        } catch (Exception e) {
            pushService.send("错误提示", "获取策略详情失败：" + e.getMessage());
        }
    }

    public void someMethod() {
        SystemConfig config = systemConfigManager.getConfig();
        if (config.isEnableYunFeiMonitor()) {
            // 执行云飞系统监控
        }
        if (config.isEnablePageChangeMonitor()) {
            // 执行页面变化监控
        }
    }
} 