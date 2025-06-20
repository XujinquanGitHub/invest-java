package com.example.invest.job;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.TradeDataModel;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 股票数据抓取客户端
 */
public class OtherClient {
    public static Map<String, TradeDataModel> getTradeData(List<String> codeList) {
        String collect = codeList.stream().collect(Collectors.joining(","));
        HttpRequest request = HttpUtil.createRequest(Method.GET, "http://hq.sinajs.cn/list=" + collect);
        request.header("Referer", "http://finance.sina.com.cn/");
        String response = request.execute().body();
        String arrays[] = response.split(";");
        String[][] stock_price = new String[arrays.length][1000];
        for (int i = 0; i < arrays.length; i++) {
            String arrays2[] = arrays[i].split(",");
            int length = arrays2.length;
            for (int j = 0; j < length; j++) {
                stock_price[i][j] = arrays2[j];
            }
        }
        Map<String, TradeDataModel> result = new HashMap<>();
        for (int i = 0; i < stock_price.length - 1; i++) {
            TradeDataModel dataModel = new TradeDataModel();
            String s = stock_price[i][0];
            String code = s.split("=")[0].replace("var hq_str_", "");
            code = code.replace("sz", "").replace("sh", "").replace("\n", "");
            dataModel.setSymbol(code);
            dataModel.setBc1(Integer.valueOf(stock_price[i][10]));
            dataModel.setBp1(new BigDecimal(stock_price[i][11]));
            dataModel.setBc2(Integer.valueOf(stock_price[i][12]));
            dataModel.setBp2(new BigDecimal(stock_price[i][13]));
            dataModel.setBc3(Integer.valueOf(stock_price[i][14]));
            dataModel.setBp3(new BigDecimal(stock_price[i][15]));
            dataModel.setBc4(Integer.valueOf(stock_price[i][16]));
            dataModel.setBp4(new BigDecimal(stock_price[i][17]));
            dataModel.setBc5(Integer.valueOf(stock_price[i][18]));
            dataModel.setBp5(new BigDecimal(stock_price[i][19]));

            dataModel.setSc1(Integer.valueOf(stock_price[i][20]));
            dataModel.setSp1(new BigDecimal(stock_price[i][21]));
            dataModel.setSc2(Integer.valueOf(stock_price[i][22]));
            dataModel.setSp2(new BigDecimal(stock_price[i][23]));
            dataModel.setSc3(Integer.valueOf(stock_price[i][24]));
            dataModel.setSp3(new BigDecimal(stock_price[i][25]));
            dataModel.setSc4(Integer.valueOf(stock_price[i][26]));
            dataModel.setSp4(new BigDecimal(stock_price[i][27]));
            dataModel.setSc5(Integer.valueOf(stock_price[i][28]));
            dataModel.setSp5(new BigDecimal(stock_price[i][29]));

            result.put(code, dataModel);
        }
        return result;
    }
} 