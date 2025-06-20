package com.example.invest.model;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FundNavModel {
    // 日期
    private String fbrq;
    // 净值
    private BigDecimal jjjz;
    //累计净值
    private BigDecimal ljjz;
} 