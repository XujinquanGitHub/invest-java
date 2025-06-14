package com.example.invest.controller;

import com.example.invest.model.WebMonitorConfig;
import com.example.invest.model.YunMonitorConfig;
import com.example.invest.util.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    
    @Resource
    private ConfigManager configManager;
    
    @GetMapping("/web-monitor")
    public List<WebMonitorConfig> getWebMonitorConfigs() {
        return configManager.loadWebMonitorConfigs();
    }
    
    @GetMapping("/yun-monitor")
    public List<YunMonitorConfig> getYunMonitorConfigs() {
        return configManager.loadYunMonitorConfigs();
    }
    
    @PostMapping("/web-monitor")
    public void saveWebMonitorConfig(@RequestBody WebMonitorConfig config) {
        configManager.saveWebMonitorConfig(config);
    }
    
    @PostMapping("/yun-monitor")
    public void saveYunMonitorConfig(@RequestBody YunMonitorConfig config) {
        configManager.saveYunMonitorConfig(config);
    }
    
    @DeleteMapping("/web-monitor/{id}")
    public void deleteWebMonitorConfig(@PathVariable String id) {
        configManager.deleteWebMonitorConfig(id);
    }
    
    @DeleteMapping("/yun-monitor/{id}")
    public void deleteYunMonitorConfig(@PathVariable String id) {
        configManager.deleteYunMonitorConfig(id);
    }
} 