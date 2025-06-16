package com.example.invest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRank {
    private String code;    // 股票代码
    private BigDecimal change;  // 涨幅
} 