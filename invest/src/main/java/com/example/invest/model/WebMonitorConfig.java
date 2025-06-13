package com.example.invest.model;

import lombok.Data;
import java.time.LocalTime;

@Data
public class WebMonitorConfig {
    private String id;          // 配置ID
    private String name;        // 配置名称
    private String url;         // 监控URL
    private String selector;    // CSS选择器
    private String description; // 描述
    private boolean enabled;    // 是否启用
    private int interval;       // 检查间隔（小时）
    private LocalTime startTime; // 开始监控时间
    private LocalTime endTime;   // 结束监控时间
    private boolean useTimeRange; // 是否使用时间范围
} 