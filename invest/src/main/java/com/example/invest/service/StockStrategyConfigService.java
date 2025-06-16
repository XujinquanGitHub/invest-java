package com.example.invest.service;

import com.alibaba.fastjson.JSON;
import com.example.invest.model.StockStrategyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Slf4j
@Service
public class StockStrategyConfigService {

    private static final String CONFIG_FILE = "D:/Projects/data/config/stock_strategy.json";
    private StockStrategyConfig config;

    @PostConstruct
    public void init() {
        loadConfig();
    }

    public void loadConfig() {
        try {
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(CONFIG_FILE)));
                config = JSON.parseObject(content, StockStrategyConfig.class);
            } else {
                // 默认配置
                config = new StockStrategyConfig();
                config.setCron("0 30 15 * * ?");
                config.setDays(20);
                config.setEnabled(true);
                config.setStockCodes(Arrays.asList(
                        "000001.SZ",  // 平安银行
                        "600000.SH",  // 浦发银行
                        "601318.SH",  // 中国平安
                        "600036.SH",  // 招商银行
                        "000858.SZ",  // 五粮液
                        "600519.SH",  // 贵州茅台
                        "000333.SZ",  // 美的集团
                        "600276.SH",  // 恒瑞医药
                        "000651.SZ",  // 格力电器
                        "600887.SH"   // 伊利股份
                ));
                saveConfig();
            }
        } catch (IOException e) {
            log.error("加载股票策略配置失败", e);
        }
    }

    public void saveConfig() {
        try {
            Files.createDirectories(Paths.get(CONFIG_FILE).getParent());
            Files.write(Paths.get(CONFIG_FILE), JSON.toJSONString(config, true).getBytes());
        } catch (IOException e) {
            log.error("保存股票策略配置失败", e);
        }
    }

    public StockStrategyConfig getConfig() {
        return config;
    }

    public void updateConfig(StockStrategyConfig newConfig) {
        this.config = newConfig;
        saveConfig();
    }
} 