package com.example.invest.model;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class FundOnModel {
    // 基金Code
    private String fund_id;
    // 基金名
    private String fund_nm;
    //基金分类
    private String qtype;
    private String issuer_nm;
    private String asset_ratio;
    // 现价
    private String price;
    private String trade_price;
    // 场内涨幅
    private String increase_rt;
    // 成交额
    private String volume;
    // 净值
    private String fund_nav;
    private String net_value;
    private String nav_dt;
    //估值
    private String estimate_value;
    private String realtime_estimate_value;
    private String est_val;
    private String est_val_increase_rt;
    private String discount_rt;
    private String index_id;
    //指数名称
    private String index_nm;
    //费率
    private String apply_fee;
    //费率提示
    private String apply_fee_tips;
    private String estimate_value2;
    private String est_val_dt;
    private String est_val_dt2;
    private String est_val_increase_rt2;
    private String discount_rt2;
    private String left_year;
    private String maturity_dt;
    private String bond_ratio;
    private String stock_ratio;
    private String fund_company;
    // 指数涨幅
    private String ref_increase_rt;
    private String type;
    //20日涨幅
    private BigDecimal twentyDayIncrease;
    private BigDecimal lastEstValue;
    private BigDecimal lastDiscount;
    private String lastDiscountString;
    private BigDecimal sell1;
    private BigDecimal buy1;
    private String estType;
    private Map<String,Integer> factorScore;
    private Integer allScore=0;
} 