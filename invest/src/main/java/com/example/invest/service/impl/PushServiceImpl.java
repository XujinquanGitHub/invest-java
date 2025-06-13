package com.example.invest.service.impl;

import com.example.invest.push.DingTalkPushUtil;
import com.example.invest.push.WeChatPushUtil;
import com.example.invest.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushServiceImpl implements PushService {
    
    @Autowired
    private DingTalkPushUtil dingTalkPushUtil;
    
    @Autowired
    private WeChatPushUtil weChatPushUtil;
    
    @Override
    public void send(String title, String content) {
        dingTalkPushUtil.send(title, content);
        weChatPushUtil.send(title, content);
    }
} 