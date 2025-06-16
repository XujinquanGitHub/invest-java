package com.example.invest.model;

import lombok.Data;

import java.util.List;

@Data
public class StockStrategyConfig {
    private String cron;  // 定时表达式
    private List<String> stockCodes;  // 股票代码列表
    private Integer days;  // 统计天数
    private Boolean enabled;  // 是否启用
} 