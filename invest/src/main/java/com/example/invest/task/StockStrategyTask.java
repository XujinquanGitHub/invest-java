package com.example.invest.task;

import com.example.invest.service.StockStrategyConfigService;
import com.example.invest.service.StockStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class StockStrategyTask {

    @Resource
    private StockStrategyService stockStrategyService;

    @Resource
    private StockStrategyConfigService configService;

    /**
     * 根据配置的定时表达式执行股票涨幅排名策略
     */
    @Scheduled(cron = "#{@stockStrategyConfigService.getConfig().cron}")
    public void executeStockRankStrategy() {
        log.info("开始执行股票涨幅排名定时任务");
        try {
            stockStrategyService.executeStockRankStrategy();
        } catch (Exception e) {
            log.error("执行股票涨幅排名策略异常", e);
        }
    }
} 