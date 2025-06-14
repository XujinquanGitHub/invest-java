package com.example.invest.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.invest.util.CookieManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 雪球网数据接收集成测试。
 * 此测试将模拟 Tampermonkey 脚本向本地通用接口发送数据。
 * 请确保您的 Spring Boot 应用程序正在运行，且 CORS 配置正确。
 */
@SpringBootTest
@Disabled // 默认禁用此集成测试，因为它需要运行的后端服务。
public class XueQiuIntegrationTest {

    @Resource
    private DataReceiverController dataReceiverController;

    @Resource
    private CookieManager cookieManager;

    @Test
    void testReceiveXueQiuDataAndSaveCookie() {
        String testDomain = "xueqiu.com";
        String testCookies = "xq_a_token=test_token; xq_r_token=test_r_token;";
        String testUrl = "https://xueqiu.com/u/12345.html";
        String testTitle = "测试用户主页 - 雪球";

        JSONObject payload = new JSONObject();
        payload.put("url", testUrl);
        payload.put("title", testTitle);
        payload.put("timestamp", Instant.now().toString());
        payload.put("website", "雪球网");

        JSONObject requestData = new JSONObject();
        requestData.put("domain", testDomain);
        requestData.put("cookies", testCookies);
        requestData.put("payload", payload);

        // 调用控制器方法，模拟接收请求
        String response = dataReceiverController.receiveData(requestData);

        // 验证控制器返回结果
        assertEquals("数据接收成功！", response);

        // 验证 Cookie 是否被保存到本地文件系统
        // 由于 CookieManager 会实际写入文件，这里我们可以尝试读取它来验证
        String savedCookies = cookieManager.getCookies(testDomain);
        assertNotNull(savedCookies, "Cookie 应该被保存");
        assertTrue(savedCookies.contains("test_token"), "保存的 Cookie 应包含测试 token");
        assertTrue(savedCookies.contains("test_r_token"), "保存的 Cookie 应包含刷新 token");

        System.out.println("雪球数据接收和 Cookie 保存测试成功！");
    }
} 