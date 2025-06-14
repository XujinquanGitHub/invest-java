package com.example.invest.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 证券涨幅工具类
 * 使用 Tushare 的 REST API 获取证券涨幅数据
 */
@Slf4j
public class StockUtil {

    private static final String TUSHARE_API_URL = "http://api.tushare.pro";

    /**
     * 获取指定证券的涨幅
     * @param code 证券代码，例如：000001.SZ
     * @param days 获取近 N 日涨幅，如果 N 为 0，则获取当日涨幅
     * @return 涨幅百分比，如果获取失败则返回 null
     */
    public static BigDecimal getStockChange(String code, int days) {
        OkHttpClient client = new OkHttpClient();
        JSONObject body = new JSONObject();
        body.put("api_name", "daily");
        body.put("token", TokenManager.getTushareToken());
        JSONObject paramObj = new JSONObject();
        paramObj.put("ts_code", code);
        if (days > 0) {
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusDays(days);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
            paramObj.put("start_date", start.format(fmt));
            paramObj.put("end_date", end.format(fmt));
        }
        body.put("params", paramObj);
        body.put("fields", "ts_code,trade_date,close,pct_chg");

        Request request = new Request.Builder()
                .url(TUSHARE_API_URL)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toJSONString(), okhttp3.MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                log.info("获取证券涨幅成功: {}", responseBody);
                JSONObject jsonResponse = JSON.parseObject(responseBody);
                if (jsonResponse.getInteger("code") == 0) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    if (data != null) {
                        JSONArray items = data.getJSONArray("items");
                        if (items != null && items.size() > 0) {
                            // 取最新和最早的收盘价，计算涨幅
                            if (days > 0 && items.size() > 1) {
                                // items 按日期倒序排列，0是最新，最后一个是最早
                                JSONArray latest = items.getJSONArray(0);
                                JSONArray oldest = items.getJSONArray(items.size() - 1);
                                BigDecimal closeLatest = latest.getBigDecimal(2); // close 是第3个字段
                                BigDecimal closeOldest = oldest.getBigDecimal(2);
                                if (closeOldest != null && closeOldest.compareTo(BigDecimal.ZERO) != 0) {
                                    return closeLatest.subtract(closeOldest)
                                            .divide(closeOldest, 6, BigDecimal.ROUND_HALF_UP)
                                            .multiply(BigDecimal.valueOf(100));
                                }
                            } else {
                                // days==0，取最新的pct_chg
                                JSONArray item = items.getJSONArray(0);
                                return item.getBigDecimal(3); // pct_chg 是第4个字段
                            }
                        }
                    }
                }
            } else {
                log.error("获取证券涨幅失败: {}", response.code());
            }
        } catch (IOException e) {
            log.error("获取证券涨幅异常", e);
        }
        return null;
    }
} 