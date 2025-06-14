package com.example.invest.modules.bond.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.modules.job.utils.CalOptionModel;
import com.example.invest.modules.job.utils.OptionPrice;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: invest
 * @description: 债券工具类
 * @author: 许金泉
 * @create: 2023-05-19 18:39
 **/
public class BondHelper {

    public static List<JSONObject>  bondList(Map<String, BigDecimal> map){
        JSONArray jsonArray = LudeClient.loadData();
        JSONArray xiaData = LudeClient.loadDataXiaLocal();

        List<JSONObject> collect = jsonArray.stream().map(i -> (JSONObject) i).filter(u -> map
                .containsKey(u.getString("code").replace(".SH", "").replace(".SZ", ""))).collect(
                Collectors.toList());
        Map<String,JSONObject> xiaColl = xiaData.stream().map(i -> (JSONObject) i).filter(u -> map
                .containsKey(u.getString("code").replace(".SH", "").replace(".SZ", ""))).collect(
                Collectors.toMap(m->m.getString("code").replace(".SH", "").replace(".SZ", ""),k->k));

        List<String> yujiXiaList  = new ArrayList<>();
        List<String> buXiaList =new ArrayList<>();

        return collect.stream().map(jsonObject->{
            String code = jsonObject.getString("code").replace(".SH", "").replace(".SZ", "");
            BigDecimal volatility_stk = jsonObject.getBigDecimal("volatility_stk");
            BigDecimal stockPrice = map.get(code);
            BigDecimal conv_price = jsonObject.getBigDecimal("conv_price");

            CalOptionModel optionModel = new CalOptionModel();
            optionModel.setCurrent(stockPrice.doubleValue());
            optionModel.setTarget(conv_price.doubleValue());
            optionModel.setYearNum(jsonObject.getDouble("left_years"));
            optionModel.setBd(volatility_stk.doubleValue());
            CalOptionModel cal = OptionPrice.cal(optionModel);
           BigDecimal pure_value = jsonObject.getBigDecimal("pure_value");
           if (pure_value==null){
               System.out.println("错误："+jsonObject.toJSONString());
               return null;
           }
           JSONObject result = new JSONObject()
                   .fluentPut("bsmValue", cal.getBsmValue().setScale(2, BigDecimal.ROUND_HALF_UP))
                   .fluentPut("mtValue", cal.getMtValue().setScale(2, BigDecimal.ROUND_HALF_UP))
                   .fluentPut("bd", volatility_stk.multiply(new BigDecimal(100))
                           .setScale(2, BigDecimal.ROUND_HALF_UP) + "%")
                   .fluentPut("name", jsonObject.getString("name"))
                   .fluentPut("yy", jsonObject.getString("yy_rating"))
                   .fluentPut("bsmPrice",
                           pure_value.add(cal.getBsmValue()).setScale(2, BigDecimal.ROUND_HALF_UP))
                   .fluentPut("mtPrice",
                           pure_value.add(cal.getMtValue()).setScale(2, BigDecimal.ROUND_HALF_UP))
                   .fluentPut("pure_value", pure_value.setScale(2, BigDecimal.ROUND_HALF_UP))
                   .fluentPut("code", code);
           JSONObject xiaSignData = xiaColl.get(code);
           if (xiaSignData!=null){
               result.fluentPut("adjust_remain_days",xiaSignData.getString("adjust_remain_days"))
                      .fluentPut("lt_bps",xiaSignData.getString("lt_bps"));
           }
           if (yujiXiaList.contains(code)){
               result.fluentPut("yjXia","1");
           }
            if (buXiaList.contains(code)){
                result.fluentPut("bXia","1");
            }
           return result;
        }).filter(u->u!=null).collect(Collectors.toList());
    }
} 