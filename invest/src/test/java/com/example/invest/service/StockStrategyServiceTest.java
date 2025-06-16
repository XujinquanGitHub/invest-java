package com.example.invest.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class StockStrategyServiceTest {

    @Resource
    private StockStrategyService stockStrategyService;

    @Test
    void testExecuteStockRankStrategy() {
        stockStrategyService.executeStockRankStrategy();
    }
} 