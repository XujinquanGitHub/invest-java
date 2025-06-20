package com.example.invest.job;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.util.CookiesUtil;
import com.example.invest.model.TradeDataModel;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public class JiSiService {
    public static JSONObject stockLofList() {
        String url = "https://www.jisilu.cn/data/lof/stock_lof_list/";
        return fundHttp(url);
    }
    public static JSONObject indexLofList() {
        String url = "https://www.jisilu.cn/data/lof/index_lof_list/";
        return fundHttp(url);
    }
    public static JSONObject etfList() {
        String url = "https://www.jisilu.cn/data/etf/etf_list/";
        return fundHttp(url);
    }
    public static JSONObject goldList() {
        String url = "https://www.jisilu.cn/data/etf/gold_list/";
        return fundHttp(url);
    }
    public static JSONObject cfList() {
        String url = "https://www.jisilu.cn/data/cf/cf_list/";
        return fundHttp(url);
    }
    public static JSONObject cfBondList() {
        String url = "https://www.jisilu.cn/data/cf/bond_list/";
        return fundHttp(url);
    }
    public static JSONObject qdiiListE() {
        String url = "https://www.jisilu.cn/data/qdii/qdii_list/E";
        return fundHttp(url);
    }
    public static JSONObject qdiiListC() {
        String url = "https://www.jisilu.cn/data/qdii/qdii_list/C";
        return fundHttp(url);
    }
    public static JSONObject qdiiListA() {
        String url = "https://www.jisilu.cn/data/qdii/qdii_list/A";
        return fundHttp(url);
    }
    public static JSONObject fundHttp(String url){
        Map<String, Object> params = new HashMap<>();
        params.put("rp", "25");
        params.put("page", "1");
        params.put("___jsl","LST___t"+System.currentTimeMillis());
        String body=HttpUtil.get(url,params);
        return JSON.parseObject(body, JSONObject.class);
    }
    public static Map<String, String> getHead() {
        Map<String, String> heads = new HashMap<>();
        heads.put("", "");
        heads.put("Accept-Encoding", "gzip, deflate, br");
        heads.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        heads.put("Connection", "keep-alive");
        heads.put("Host", "www.jisilu.cn");
        String cookie = CookiesUtil.getCookie("www.jisilu.cn");
        if (StringUtils.isNotBlank(cookie)){
            heads.put("cookie", cookie);
        }
        heads.put("Origin", "https://www.jisilu.cn");
        heads.put("Sec-Fetch-Dest", "empty");
        heads.put("Sec-Fetch-Mode", "cors");
        heads.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        heads.put("Sec-Fetch-Site", "same-site");
        heads.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
        return heads;
    }
    public static TradeDataModel getTradeData(String code) {
        String url = "https://www.jisilu.cn/data/fund/get_fund_quote_list/"+code;
        Map<String, Object> params = new HashMap<>();
        params.put("rp", "25");
        params.put("page", "1");
        params.put("___jsl","LST___t"+System.currentTimeMillis());
        String body=HttpUtil.get(url,params);
        JSONArray rows = JSON.parseObject(body, JSONObject.class).getJSONArray("rows");
        TradeDataModel dataModel = new TradeDataModel();
        rows.forEach(item->{
            JSONObject jsonObject = (JSONObject) item;
            String id = jsonObject.getString("id");
            JSONObject cell = jsonObject.getJSONObject("cell");
            BigDecimal q_price = cell.getBigDecimal("q_price");
            BigDecimal q_amount = cell.getBigDecimal("q_amount");
            switch (id){
                case "sell1":
                    dataModel.setSp1(q_price);
                    dataModel.setSc1(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "sell2":
                    dataModel.setSp2(q_price);
                    dataModel.setSc2(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "sell3":
                    dataModel.setSp3(q_price);
                    dataModel.setSc3(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "sell4":
                    dataModel.setSp4(q_price);
                    dataModel.setSc4(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "sell5":
                    dataModel.setSp5(q_price);
                    dataModel.setSc5(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "buy1":
                    dataModel.setBp1(q_price);
                    dataModel.setBc1(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "buy2":
                    dataModel.setBp2(q_price);
                    dataModel.setBc2(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "buy3":
                    dataModel.setBp3(q_price);
                    dataModel.setBc3(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "buy4":
                    dataModel.setBp4(q_price);
                    dataModel.setBc4(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                case "buy5":
                    dataModel.setBp5(q_price);
                    dataModel.setBc5(q_amount.multiply(new BigDecimal(10000)).divide(q_price,2,BigDecimal.ROUND_HALF_UP).intValue());
                    break;
                default:
                    break;
            }
        });
        return dataModel;
    }
} 