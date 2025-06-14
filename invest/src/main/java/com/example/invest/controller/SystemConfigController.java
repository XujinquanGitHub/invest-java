package com.example.invest.controller;

import com.example.invest.model.SystemConfig;
import com.example.invest.util.SystemConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/system")
public class SystemConfigController {
    
    @Resource
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