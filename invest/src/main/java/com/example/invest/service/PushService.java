package com.example.invest.service;

public interface PushService {
    /**
     * 推送普通消息
     * @param title 消息标题
     * @param content 消息内容
     */
    void pushMessage(String title, String content);

    /**
     * 推送错误消息
     * @param title 消息标题
     * @param error 错误信息
     */
    void pushError(String title, String error);
} 