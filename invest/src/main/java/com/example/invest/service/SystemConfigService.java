package com.example.invest.service;

import com.example.invest.model.SystemConfig;
import com.example.invest.util.SystemConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SystemConfigService {

    @Resource
    private SystemConfigManager systemConfigManager;

    public SystemConfig getSystemConfig() {
        return systemConfigManager.getConfig();
    }

    public void saveSystemConfig(SystemConfig config) {
        systemConfigManager.saveConfig(config);
    }
}
