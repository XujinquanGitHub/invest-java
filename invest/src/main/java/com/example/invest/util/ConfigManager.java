package com.example.invest.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.invest.model.WebMonitorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ConfigManager {
    private static final String CONFIG_FILE = "D:\\Projects\\data\\config\\web_monitor.json";
    
    /**
     * 保存配置
     */
    public void saveConfig(WebMonitorConfig config) {
        log.info("开始保存网页监控配置: {}", config.getName());
        List<WebMonitorConfig> configs = loadConfigs();
        log.info("当前加载的配置数量: {}", configs.size());
        
        // 如果是新配置，生成ID
        if (config.getId() == null || config.getId().isEmpty()) {
            config.setId(UUID.randomUUID().toString());
            configs.add(config);
        } else {
            // 更新现有配置
            for (int i = 0; i < configs.size(); i++) {
                if (configs.get(i).getId().equals(config.getId())) {
                    configs.set(i, config);
                    break;
                }
            }
        }
        
        // 保存到文件
        try {
            File file = new File(CONFIG_FILE);
            if (!file.getParentFile().exists()) {
                log.info("创建配置目录: {}", file.getParentFile().getAbsolutePath());
                file.getParentFile().mkdirs();
            }
            
            String jsonContent = JSON.toJSONString(configs, true);
            log.info("准备写入文件 {} 的配置数据: {}", CONFIG_FILE, jsonContent);
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8)) {
                writer.write(jsonContent);
                log.info("配置数据成功写入文件: {}", CONFIG_FILE);
            }
        } catch (IOException e) {
            log.error("保存网页监控配置失败：" + CONFIG_FILE, e);
            throw new RuntimeException("保存配置失败", e);
        }
    }
    
    /**
     * 加载所有配置
     */
    public List<WebMonitorConfig> loadConfigs() {
        log.info("开始加载网页监控配置: {}", CONFIG_FILE);
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            log.warn("配置文件不存在，返回空列表: {}", CONFIG_FILE);
            return new ArrayList<>();
        }
        
        try {
            try (InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8)) {
                StringBuilder content = new StringBuilder();
                char[] buffer = new char[1024];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    content.append(buffer, 0, read);
                }
                String fileContent = content.toString();
                log.info("读取到配置文件 {} 的内容: {}", CONFIG_FILE, fileContent);

                List<WebMonitorConfig> loadedConfigs = JSON.parseArray(fileContent, WebMonitorConfig.class);
                log.info("成功加载 {} 条配置。", loadedConfigs != null ? loadedConfigs.size() : 0);
                return loadedConfigs;
            }
        } catch (IOException e) {
            log.error("加载网页监控配置失败：" + CONFIG_FILE, e);
            return new ArrayList<>(); // 加载失败返回空列表，不中断应用
        }
    }
    
    /**
     * 删除配置
     */
    public void deleteConfig(String id) {
        List<WebMonitorConfig> configs = loadConfigs();
        configs.removeIf(config -> config.getId().equals(id));
        
        try {
            File file = new File(CONFIG_FILE);
            String jsonContent = JSON.toJSONString(configs, true);
            log.info("准备写入文件 {} 的删除后配置数据: {}", CONFIG_FILE, jsonContent);
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8)) {
                writer.write(jsonContent);
                log.info("配置数据成功写入文件: {}", CONFIG_FILE);
            }
        } catch (IOException e) {
            log.error("删除网页监控配置失败：" + CONFIG_FILE, e);
            throw new RuntimeException("删除配置失败", e);
        }
    }
} 