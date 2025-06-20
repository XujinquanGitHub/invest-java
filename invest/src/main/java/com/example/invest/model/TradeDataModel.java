package com.example.invest.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TradeDataModel {
    private String symbol;
    private String timestamp;
    // 买一
    private BigDecimal bp1;
    //买一数
    private Integer bc1;
    private BigDecimal bp2;
    private Integer bc2;
    private BigDecimal bp3;
    private Integer bc3;
    private BigDecimal bp4;
    private Integer bc4;
    private BigDecimal bp5;
    private Integer bc5;
    private BigDecimal current;
    // 卖一
    private BigDecimal sp1;
    //卖一数
    private Integer sc1;
    private BigDecimal sp2;
    private Integer sc2;
    private BigDecimal sp3;
    private Integer sc3;
    private BigDecimal sp4;
    private Integer sc4;
    private BigDecimal sp5;
    private Integer sc5;
} 