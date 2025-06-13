package com.example.invest.controller;

import com.example.invest.model.SystemConfig;
import com.example.invest.util.SystemConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
public class SystemConfigController {
    
    @Autowired
    private SystemConfigManager systemConfigManager;
    
    @GetMapping("/config")
    public SystemConfig getConfig() {
        return systemConfigManager.getConfig();
    }
    
    @PostMapping("/config")
    public void saveConfig(@RequestBody SystemConfig config) {
        systemConfigManager.saveConfig(config);
    }
} 