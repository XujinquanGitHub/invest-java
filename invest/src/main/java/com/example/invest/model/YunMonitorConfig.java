package com.example.invest.model;

import lombok.Data;

@Data
public class YunMonitorConfig {
    private String id;
    private String name;
    private String url;
    private String selector;
    private boolean enabled;
    private int interval;
    private boolean useTimeRange;
    private int startHour;
    private int endHour;
} 