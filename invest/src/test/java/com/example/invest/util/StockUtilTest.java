package com.example.invest.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

/**
 * StockUtil 集成测试类
 */
public class StockUtilTest {

    @Test
    public void testGetTodayStockChange() {
        BigDecimal change = StockUtil.getStockChange("000001.SZ", 0);
        assertNotNull(change, "当日涨幅不应为 null");
        System.out.println("当日涨幅: " + change + "%");
    }

    @Test
    public void testGetFiveDayStockChange() {
        BigDecimal change = StockUtil.getStockChange("000001.SZ", 5);
        assertNotNull(change, "近 5 日涨幅不应为 null");
        System.out.println("近 5 日涨幅: " + change + "%");
    }
} 