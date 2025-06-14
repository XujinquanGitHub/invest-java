package com.example.invest.util;

import com.alibaba.fastjson.JSON;
import com.example.invest.model.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Slf4j
@Component
public class SystemConfigManager {
    private static final String CONFIG_FILE = "D:\\Projects\\data\\config\\system_config.json";
    private SystemConfig config;
    
    public SystemConfigManager() {
        loadConfig();
    }
    
    public SystemConfig getConfig() {
        return config;
    }
    
    public void saveConfig(SystemConfig config) {
        try {
            File configDir = new File("D:\\Projects\\data\\config");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            Files.write(Paths.get(CONFIG_FILE), JSON.toJSONString(config).getBytes());
            this.config = config;
        } catch (IOException e) {
            log.error("保存系统配置失败", e);
        }
    }
    
    private void loadConfig() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                config = new SystemConfig();
                config.setEnableYunFeiMonitor(true);
                config.setEnablePageChangeMonitor(true);
                config.setEnableGuorenMonitor(false);
                config.setConfigPath("D:\\Projects\\data\\config");
                config.setWechatPushToken("your_wechat_push_token");
                config.setWechatTemplateId("your_wechat_template_id");
                config.setGuorenConfigs(new ArrayList<>());
                saveConfig(config);
                return;
            }
            String content = new String(Files.readAllBytes(Paths.get(CONFIG_FILE)));
            config = JSON.parseObject(content, SystemConfig.class);
            if (config.getGuorenConfigs() == null) {
                config.setGuorenConfigs(new ArrayList<>());
            }
        } catch (IOException e) {
            log.error("加载系统配置失败", e);
            config = new SystemConfig();
            config.setEnableYunFeiMonitor(true);
            config.setEnablePageChangeMonitor(true);
            config.setEnableGuorenMonitor(false);
            config.setConfigPath("D:\\Projects\\data\\config");
            config.setWechatPushToken("your_wechat_push_token");
            config.setWechatTemplateId("your_wechat_template_id");
            config.setGuorenConfigs(new ArrayList<>());
        }
    }
} 