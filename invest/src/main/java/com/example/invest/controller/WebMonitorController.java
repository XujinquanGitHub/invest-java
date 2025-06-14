package com.example.invest.controller;

import com.example.invest.model.WebMonitorConfig;
import com.example.invest.service.WebMonitorService;
import com.example.invest.util.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/web-monitor")
public class WebMonitorController {

    @Autowired
    private WebMonitorService webMonitorService;

    @Autowired
    private ConfigManager configManager;

    @GetMapping("/configs")
    public List<WebMonitorConfig> getConfigs() {
        return configManager.loadWebMonitorConfigs();
    }

    @PostMapping("/config")
    public void saveConfig(@RequestBody WebMonitorConfig config) {
        configManager.saveWebMonitorConfig(config);
    }

    @DeleteMapping("/config/{id}")
    public void deleteConfig(@PathVariable String id) {
        configManager.deleteWebMonitorConfig(id);
    }

    @PostMapping("/start")
    public void startMonitoring() {
        webMonitorService.startMonitoring();
    }

    @PostMapping("/stop")
    public void stopMonitoring() {
        webMonitorService.stopMonitoring();
    }

    // 手动执行监控
    @PostMapping("/execute/{id}")
    public ResponseEntity<String> executeMonitorManually(@PathVariable String id) {
        webMonitorService.executeMonitorManually(id);
        return ResponseEntity.ok("手动执行监控已触发");
    }
} 