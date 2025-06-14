package com.example.invest.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.WebMonitorConfig;
import com.example.invest.model.YunMonitorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ConfigManager {

    private static final String CONFIG_DIR = "config";
    private static final String WEB_MONITOR_CONFIG_FILE = CONFIG_DIR + "/web_monitor.json";
    private static final String YUN_MONITOR_CONFIG_FILE = CONFIG_DIR + "/yun_monitor.json";

    public ConfigManager() {
        createConfigDirIfNotExists();
    }

    private void createConfigDirIfNotExists() {
        File dir = new File(CONFIG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // WebMonitorConfig 相关方法
    public List<WebMonitorConfig> loadWebMonitorConfigs() {
        try {
            if (!Files.exists(Paths.get(WEB_MONITOR_CONFIG_FILE))) {
                return new ArrayList<>();
            }
            String content = new String(Files.readAllBytes(Paths.get(WEB_MONITOR_CONFIG_FILE)));
            return JSON.parseArray(content, WebMonitorConfig.class);
        } catch (IOException e) {
            log.error("加载网页监控配置失败", e);
            return new ArrayList<>();
        }
    }

    public void saveWebMonitorConfig(WebMonitorConfig config) {
        try {
            List<WebMonitorConfig> configs = loadWebMonitorConfigs();
            if (config.getId() == null || config.getId().isEmpty()) {
                config.setId(UUID.randomUUID().toString());
            }
            configs.add(config);
            Files.write(Paths.get(WEB_MONITOR_CONFIG_FILE), JSON.toJSONString(configs).getBytes());
        } catch (IOException e) {
            log.error("保存网页监控配置失败", e);
        }
    }

    public void deleteWebMonitorConfig(String id) {
        try {
            List<WebMonitorConfig> configs = loadWebMonitorConfigs();
            configs.removeIf(config -> config.getId().equals(id));
            Files.write(Paths.get(WEB_MONITOR_CONFIG_FILE), JSON.toJSONString(configs).getBytes());
        } catch (IOException e) {
            log.error("删除网页监控配置失败", e);
        }
    }

    // YunMonitorConfig 相关方法
    public List<YunMonitorConfig> loadYunMonitorConfigs() {
        try {
            if (!Files.exists(Paths.get(YUN_MONITOR_CONFIG_FILE))) {
                return new ArrayList<>();
            }
            String content = new String(Files.readAllBytes(Paths.get(YUN_MONITOR_CONFIG_FILE)));
            return JSON.parseArray(content, YunMonitorConfig.class);
        } catch (IOException e) {
            log.error("加载云飞监控配置失败", e);
            return new ArrayList<>();
        }
    }

    public void saveYunMonitorConfig(YunMonitorConfig config) {
        try {
            List<YunMonitorConfig> configs = loadYunMonitorConfigs();
            if (config.getId() == null || config.getId().isEmpty()) {
                config.setId(UUID.randomUUID().toString());
            }
            configs.add(config);
            Files.write(Paths.get(YUN_MONITOR_CONFIG_FILE), JSON.toJSONString(configs).getBytes());
        } catch (IOException e) {
            log.error("保存云飞监控配置失败", e);
        }
    }

    public void deleteYunMonitorConfig(String id) {
        try {
            List<YunMonitorConfig> configs = loadYunMonitorConfigs();
            configs.removeIf(config -> config.getId().equals(id));
            Files.write(Paths.get(YUN_MONITOR_CONFIG_FILE), JSON.toJSONString(configs).getBytes());
        } catch (IOException e) {
            log.error("删除云飞监控配置失败", e);
        }
    }
} 