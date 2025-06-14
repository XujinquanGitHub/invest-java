package com.example.invest.modules.job.utils;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @program: invest
 * @description: 期权计算模型
 * @author: 许金泉
 * @create: 2023-05-19 17:11
 **/
@Data
public class CalOptionModel {

   private double current;

   private double target;

   private double lx=0.0171;

   private double bd;

   private double yearNum;

   private BigDecimal bsmValue;

   private BigDecimal mtValue;
} 