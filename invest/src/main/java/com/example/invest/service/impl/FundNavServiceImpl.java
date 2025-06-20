package com.example.invest.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.FundNavModel;
import com.example.invest.service.FundNavService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FundNavServiceImpl implements FundNavService {
    private Map<String, List<FundNavModel>> data = new HashMap<>();

    @Override
    public Map<String, List<FundNavModel>> loadAllNav() {
        String s = FileUtil.readString("D:\\Projects\\data\\nav.json", "UTF-8");
        JSONObject jsonObject = JSON.parseObject(s, JSONObject.class);
        String date = jsonObject.getString("date");
        String s1 = DateUtil.formatDate(new Date());
        if (date.equalsIgnoreCase(s1)) {
            Map<String, List<FundNavModel>> dataLocal = jsonObject.getJSONObject("data").entrySet().stream().collect(
                    Collectors.toMap(u -> u.getKey(),
                            u -> JSON.parseArray(JSON.toJSONString(u.getValue()), FundNavModel.class)));
            data.putAll(dataLocal);
            return data;
        } else {
            // 这里需要 allFund 数据，建议通过参数传递或注入依赖
            // allFund.forEach(item -> ...)
            // 这里只迁移方法，具体实现可后续完善
            return data;
        }
    }

    @Override
    public List<FundNavModel> loadFundNav(String fundId) {
        return loadTwentyDayFundNavForSiNa(fundId);
    }

    @Override
    public List<FundNavModel> loadTwentyDayFundNavForSiNa(String fundId) {
        List<FundNavModel> result = new ArrayList<>();
        try {
            String url = "http://fundf10.eastmoney.com/F10DataApi.aspx?type=lsjz&code=" + fundId + "&page=1&per=20";
            String html = HttpUtil.get(url);
            Document doc = Jsoup.parse(html);
            Elements trs = doc.select("tbody tr");
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() < 7) continue;
                FundNavModel model = new FundNavModel();
                model.setFbrq(tds.get(0).text());
                model.setJjjz(new BigDecimal(tds.get(1).text()));
                model.setLjjz(new BigDecimal(tds.get(2).text()));
                result.add(model);
            }
        } catch (Exception e) {
            System.out.println("loadTwentyDayFundNavForSiNa error: " + fundId + e.getMessage());
        }
        return result;
    }
} 