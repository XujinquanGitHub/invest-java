package com.example.invest.model;

import lombok.Data;
import java.util.List;

@Data
public class GuorenConfig {
    // 这里写果仁网相关字段
    private String username;
    private String password;
    private String strategyIds;
    private String monitorTimes;
    private String monitorWeekdays;
    private boolean enabled;
}


