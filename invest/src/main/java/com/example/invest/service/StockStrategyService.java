package com.example.invest.service;

import com.example.invest.model.StockRank;
import com.example.invest.model.StockStrategyConfig;
import com.example.invest.push.DingTalkPushUtil;
import com.example.invest.util.StockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class StockStrategyService {

    @Resource
    private DingTalkPushUtil dingTalkPushUtil;

    @Resource
    private StockStrategyConfigService configService;

    /**
     * 执行股票涨幅排名策略
     */
    public void executeStockRankStrategy() {
        log.info("开始执行股票涨幅排名策略");
        StockStrategyConfig config = configService.getConfig();
        if (!config.getEnabled()) {
            log.info("股票涨幅排名策略已禁用");
            return;
        }

        List<StockRank> stockRanks = new ArrayList<>();

        // 获取每只股票的涨幅
        for (String stockCode : config.getStockCodes()) {
            BigDecimal change = StockUtil.getStockChange(stockCode, config.getDays());
            if (change != null) {
                stockRanks.add(new StockRank(stockCode, change));
            }
        }

        // 按涨幅从大到小排序
        stockRanks.sort(Comparator.comparing(StockRank::getChange).reversed());

        // 生成推送内容
        StringBuilder content = new StringBuilder();
        content.append(String.format("### 近%d日涨幅排名\n\n", config.getDays()));
        content.append("| 排名 | 股票代码 | 涨幅 |\n");
        content.append("| --- | --- | --- |\n");

        for (int i = 0; i < stockRanks.size(); i++) {
            StockRank rank = stockRanks.get(i);
            content.append(String.format("| %d | %s | %.2f%% |\n",
                    i + 1, rank.getCode(), rank.getChange()));
        }

        // 推送结果
        dingTalkPushUtil.send("股票涨幅排名", content.toString());
        log.info("股票涨幅排名策略执行完成");
    }
} 