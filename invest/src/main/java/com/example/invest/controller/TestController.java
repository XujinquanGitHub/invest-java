package com.example.invest.controller;

import com.example.invest.service.PushService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    private PushService pushService;

    @GetMapping("/push")
    public String testPush() {
        try {
            // 测试普通消息推送
            pushService.pushMessage("测试消息", "这是一条测试消息，用于验证推送功能是否正常工作。");
            
            // 测试错误消息推送
            pushService.pushError("测试错误", "这是一条测试错误消息，用于验证错误推送功能是否正常工作。");
            
            return "推送测试完成，请查看微信消息";
        } catch (Exception e) {
            return "推送测试失败：" + e.getMessage();
        }
    }
} 