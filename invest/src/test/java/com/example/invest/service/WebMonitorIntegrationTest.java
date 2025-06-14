package com.example.invest.service;

import com.example.invest.model.WebMonitorConfig;
import com.example.invest.util.ConfigManager;
import com.example.invest.service.CookieManager;
import com.example.invest.util.SystemConfigManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 网页监控功能集成测试。
 * 此测试将验证 WebMonitorService 在处理网页内容和Cookie时的真实行为。
 * **重要：此测试将从配置文件中读取 WebMonitorConfig，而不是使用硬编码的数据。**
 * 请确保您的 `config/web_monitor.json` 中包含一个用于 `http://httpbin.org/headers` 的有效配置。
 * 请确保您的 Spring Boot 应用程序正在运行，并且网络连接正常。
 */
@SpringBootTest
@Disabled // 默认禁用此集成测试，因为它需要真实的网络请求。
public class WebMonitorIntegrationTest {

    @Resource
    private WebMonitorService webMonitorService;

    @Resource
    private ConfigManager configManager;

    @Resource
    private CookieManager cookieManager;

    @Resource
    private SystemConfigManager systemConfigManager; // 用于启用网页监控



    @BeforeEach
    void setUp() {
        // 启用页面变化监控，确保 WebMonitorService 的 checkConfigs 会执行
        systemConfigManager.getConfig().setEnablePageChangeMonitor(true);
        systemConfigManager.saveConfig(systemConfigManager.getConfig()); // 确保配置保存

    }

    @Test
    void testWebMonitorWithCookieFromConfig() throws InterruptedException {
        System.out.println("开始执行网页监控集成测试 (读取配置文件中的配置并带Cookie)...请查看服务器端日志，确认Cookie是否被发送。");

        // 验证配置文件中是否存在目标配置
        List<WebMonitorConfig> configs = configManager.loadWebMonitorConfigs();
        WebMonitorConfig targetConfig = configs.get(0);

        assertTrue(targetConfig.isEnabled(), "请确保 config/web_monitor.json 中测试配置是启用的！");

        // 手动调用 checkConfigs，模拟定时任务执行
        webMonitorService.checkConfigs();

        // 给予一些时间让异步操作（HTTP请求）完成
        Thread.sleep(5000); 

        // 验证：这里我们无法直接拦截HTTP请求来检查Cookie头。
        // 但是，我们可以依赖 WebMonitorService 内部的日志输出。
        // 如果你看到服务器端日志输出 '为域名 httpbin.org 添加 Cookie: {testcookie}'，
        // 则表明Cookie被正确读取并添加到Jsoup请求中。
        System.out.println("测试结束。请检查服务器端日志中是否有关于Cookie发送的log.info信息：'为域名 httpbin.org 添加 Cookie: {testcookie}'");
        
        // 简单的断言，确保测试配置被处理，并且没有立即抛出异常
        assertNotNull(targetConfig); // 再次断言，确保配置存在
    }

}