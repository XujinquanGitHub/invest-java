package com.example.invest.controller;

import com.example.invest.model.StockStrategyConfig;
import com.example.invest.service.StockStrategyConfigService;
import com.example.invest.service.StockStrategyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/stock-strategy")
public class StockStrategyController {

    @Resource
    private StockStrategyConfigService configService;

    @Resource
    private StockStrategyService strategyService;

    @GetMapping("/config")
    public StockStrategyConfig getConfig() {
        return configService.getConfig();
    }

    @PostMapping("/config")
    public void updateConfig(@RequestBody StockStrategyConfig config) {
        configService.updateConfig(config);
    }

    @PostMapping("/execute")
    public void executeStrategy() {
        strategyService.executeStockRankStrategy();
    }
} 