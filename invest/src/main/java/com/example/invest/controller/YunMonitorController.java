package com.example.invest.controller;

import com.example.invest.model.YunMonitorConfig;
import com.example.invest.service.YunMonitorService;
import com.example.invest.util.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/yun-monitor")
public class YunMonitorController {

    @Resource
    private YunMonitorService yunMonitorService;

    @Resource
    private ConfigManager configManager;

    @GetMapping("/configs")
    public List<YunMonitorConfig> getAllConfigs() {
        return configManager.loadYunMonitorConfigs();
    }

    @PostMapping("/configs")
    public ResponseEntity<String> saveConfig(@RequestBody YunMonitorConfig config) {
        configManager.saveYunMonitorConfig(config);
        return ResponseEntity.ok("配置已保存");
    }

    @DeleteMapping("/configs/{id}")
    public ResponseEntity<String> deleteConfig(@PathVariable String id) {
        configManager.deleteYunMonitorConfig(id);
        return ResponseEntity.ok("配置已删除");
    }

    // 手动执行监控
    @PostMapping("/execute/{id}")
    public ResponseEntity<String> executeMonitorManually(@PathVariable String id) {
        yunMonitorService.executeMonitorManually(id);
        return ResponseEntity.ok("手动执行监控已触发");
    }
} 