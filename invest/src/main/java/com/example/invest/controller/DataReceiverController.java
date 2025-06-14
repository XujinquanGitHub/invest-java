package com.example.invest.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.invest.util.CookieManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/data")
public class DataReceiverController {

    @Resource
    private CookieManager cookieManager;

    @PostMapping("/receive")
    public String receiveData(@RequestBody JSONObject data) {
        String domain = data.getString("domain");
        String cookies = data.getString("cookies");
        JSONObject payload = data.getJSONObject("payload"); // 通用数据载荷

        if (domain == null || domain.isEmpty() || cookies == null || cookies.isEmpty()) {
            log.warn("接收到的数据不完整：domain 或 cookies 字段缺失。数据: {}", data.toJSONString());
            return "数据不完整，接收失败！";
        }

        log.info("收到来自 [{}] 的数据。Cookie: {}, 载荷: {}", domain, cookies, payload != null ? payload.toJSONString() : "无");

        // 保存 Cookie
        cookieManager.saveCookies(domain, cookies);

        // TODO: 在这里你可以根据 payload 的内容，添加其他网站数据的存储逻辑
        // 例如，如果 payload 包含股票数据，可以解析并存储到数据库或特定文件。

        return "数据接收成功！";
    }
} 