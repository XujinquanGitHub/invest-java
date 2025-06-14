package com.example.invest.controller;

import com.example.invest.model.WebMonitorConfig;
import com.example.invest.util.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/configs")
public class ConfigController {
    
    @Resource
    private ConfigManager configManager;
    
    @GetMapping
    public List<WebMonitorConfig> getAllConfigs() {
        return configManager.loadConfigs();
    }
    
    @GetMapping("/{id}")
    public WebMonitorConfig getConfig(@PathVariable String id) {
        return configManager.loadConfigs().stream()
                .filter(config -> config.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @PostMapping
    public ResponseEntity<String> saveConfig(@RequestBody WebMonitorConfig config) {
        try {
            configManager.saveConfig(config);
            return new ResponseEntity<>("配置保存成功", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConfig(@PathVariable String id) {
        try {
            configManager.deleteConfig(id);
            return new ResponseEntity<>("配置删除成功", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 