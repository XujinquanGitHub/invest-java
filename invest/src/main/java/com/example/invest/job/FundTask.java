package com.example.invest.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.FundNavModel;
import com.example.invest.model.FundOnModel;
import com.example.invest.model.TradeDataModel;
import com.example.invest.service.FundNavService;
import com.example.invest.util.FundEstimateUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FundTask {
    @Autowired
    private FundNavService fundNavService;

    private static Map<String, List<FundNavModel>> data = new HashMap<>();

    public List<FundOnModel> queryFundList(String list) {
        List<String> myCodeList = Arrays.asList(list.split(","));
        fundNavService.loadAllNav();
        JSONArray allFund = getJSLAllFund();
        List<FundOnModel> collect = convertAndFilterFundList(allFund, myCodeList);

        Map<String, TradeDataModel> tradeMap = fetchTradeData(collect);
        List<JSONObject> fundNavInfo = getFundNavInfo();
        Map<String, BigDecimal> decimalMap = calXiaoYuIndexIncreate(fundNavInfo, collect);

        enrichFundList(collect, myCodeList, tradeMap, fundNavInfo, decimalMap);

        collect = filterValidFundList(collect);
        collect = addScore(collect, Comparator.comparing(FundOnModel::getTwentyDayIncrease));
        collect = addScore(collect, Comparator.comparing(FundOnModel::getLastDiscount).reversed());
        collect = collect.stream().sorted(Comparator.comparing(FundOnModel::getAllScore).reversed())
                .collect(Collectors.toList());
        return collect;
    }

    private List<FundOnModel> convertAndFilterFundList(JSONArray allFund, List<String> myCodeList) {
        List<FundOnModel> collect = allFund.stream().map(u -> (JSONObject) u)
                .map(u -> u.toJavaObject(FundOnModel.class)).collect(Collectors.toList());
        return filterFundList(collect, myCodeList);
    }

    private Map<String, TradeDataModel> fetchTradeData(List<FundOnModel> collect) {
        CompletionService<Map<String, TradeDataModel>> completionService =
                new ExecutorCompletionService<>(Executors.newFixedThreadPool(3));
        List<List<String>> split =
                CollectionUtil.split(collect.stream().map(u -> getAddCode(u.getFund_id())).collect(Collectors.toList()), 100);
        split.forEach(codeList -> completionService.submit(() -> OtherClient.getTradeData(codeList)));
        Map<String, TradeDataModel> tradeMap = new HashMap<>();
        split.forEach(cinemaGroup -> {
            try {
                tradeMap.putAll(completionService.take().get());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        return tradeMap;
    }

    private void enrichFundList(List<FundOnModel> collect, List<String> myCodeList,
                                Map<String, TradeDataModel> tradeMap,
                                List<JSONObject> fundNavInfo, Map<String, BigDecimal> decimalMap) {
        collect.forEach(item -> {
            insertAll(item);
            JSONObject lastEstValue = FundEstimateUtil.getEstValue(item, fundNavInfo, decimalMap);
            BigDecimal estValue = lastEstValue.getBigDecimal("estValue");
            if (estValue == null || estValue.equals(new BigDecimal(0))) {
                return;
            }
            item.setLastEstValue(estValue.setScale(3, BigDecimal.ROUND_HALF_UP));
            item.setEstType(lastEstValue.getString("estType"));
            BigDecimal priceAdd = new BigDecimal(item.getPrice()).add(new BigDecimal(0.001));
            TradeDataModel dataModel = tradeMap.get(item.getFund_id());
            if (dataModel == null || dataModel.getSp1() == null) {
                System.out.println("fundId:" + item.getFund_id());
            } else {
                if (myCodeList.contains(item.getFund_id())) {
                    priceAdd = dataModel.getBp1();
                    BigDecimal subtract = new BigDecimal(1).subtract(new BigDecimal(0.00006));
                    priceAdd = priceAdd.multiply(subtract);
                } else {
                    priceAdd = dataModel.getSp1();
                    BigDecimal subtract = new BigDecimal(1).add(new BigDecimal(0.00006));
                    priceAdd = priceAdd.multiply(subtract);
                }
                item.setSell1(dataModel.getSp1());
                item.setBuy1(dataModel.getBp1());
            }
            BigDecimal lastDiscount =
                    priceAdd.divide(estValue, 8, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal(1));
            item.setLastDiscount(lastDiscount);
            item.setLastDiscountString(
                    lastDiscount.multiply(new BigDecimal(100)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
            item.setTwentyDayIncrease(
                    calTwentyNavIncrease(item).multiply(new BigDecimal(100)).setScale(3, BigDecimal.ROUND_HALF_UP));
            item.setIncrease_rt(item.getIncrease_rt().replace("%", ""));
            if (StringUtils.isBlank(item.getFund_nav())) {
                item.setFund_nav(item.getNet_value());
            }
        });
    }

    private List<FundOnModel> filterValidFundList(List<FundOnModel> collect) {
        return collect.stream().filter(u -> u.getTwentyDayIncrease() != null && u.getLastDiscount() != null)
                .collect(Collectors.toList());
    }

    private List<FundOnModel> filterFundList(List<FundOnModel> collect, List<String> myCodeList) {
        return collect.stream().filter(u ->
                (StringUtils.isNotBlank(u.getVolume()) && NumberUtil.isNumber(u.getVolume()) &&
                        new BigDecimal(u.getVolume()).doubleValue() > 100) || myCodeList.contains(u.getFund_id()))
                .collect(Collectors.toList());
    }

    private static JSONArray getJSLAllFund() {
        JSONArray allFund = new JSONArray();
        JSONObject stockLofList = JiSiService.stockLofList();
        JSONObject indexLofList = JiSiService.indexLofList();
        JSONObject etfList = JiSiService.etfList();
        JSONObject goldList = JiSiService.goldList();
        JSONObject qdiiListE = JiSiService.qdiiListE();
        JSONObject qdiiListA = JiSiService.qdiiListA();
        JSONObject qdiiListC = JiSiService.qdiiListC();
        JSONObject cfList = JiSiService.cfList();
        JSONObject cbBondList = JiSiService.cfBondList();
        addType(allFund, stockLofList.getJSONArray("rows"), "stockLof");
        addType(allFund, indexLofList.getJSONArray("rows"), "indexLof");
        addType(allFund, etfList.getJSONArray("rows"), "etf");
        addType(allFund, goldList.getJSONArray("rows"), "gold");
        addType(allFund, qdiiListE.getJSONArray("rows"), "qdiiE");
        addType(allFund, qdiiListA.getJSONArray("rows"), "qdiiA");
        addType(allFund, qdiiListC.getJSONArray("rows"), "qdiiC");
        addType(allFund, cfList.getJSONArray("rows"), "cf");
        addType(allFund, cbBondList.getJSONArray("rows"), "cbBond");
        return allFund;
    }

    public static void addType(JSONArray total, JSONArray data, String type) {
        data.forEach(item -> {
            JSONObject item1 = (JSONObject) item;
            JSONObject cell = item1.getJSONObject("cell");
            cell.put("type", type);
            total.add(cell);
        });
    }

    public static Map<String, List<FundNavModel>> loadAllNav() {
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
            JSONArray allFund = getJSLAllFund();
            allFund.forEach(item -> {
                JSONObject object = (JSONObject) item;
                Integer id = object.getInteger("fund_id");
                List<FundNavModel> fundNavModels = loadFundNav(id.toString());
                data.put(id.toString(), fundNavModels);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            File file = new File("D:\\Projects\\data\\nav.json");
            JSONObject jsonObjectL =
                    new JSONObject().fluentPut("data", data).fluentPut("date", DateUtil.formatDate(new Date()));
            FileUtil.writeString(jsonObjectL.toJSONString(), file, "UTF-8");
            return data;
        }
    }

    public static BigDecimal calIncrease(BigDecimal startValue, BigDecimal endValue) {
        return endValue.divide(startValue, 8, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal calTwentyNavIncrease(FundOnModel item) {
        List<FundNavModel> fundNavModels = data.get(item.getFund_id());
        if (CollUtil.isEmpty(fundNavModels)) {
            System.out.println("没有查到净值:" + item.getFund_id());
            return new BigDecimal(-100000);
        }
        switch (item.getType()) {
            case "qdiiE":
            case "qdiiC":
                try {
                    FundNavModel firstData = fundNavModels.get(17);
                    FundNavModel lastFundNav = fundNavModels.get(0);
                    BigDecimal foundIncrease =
                            lastFundNav.getLjjz().divide(firstData.getLjjz(), 8, BigDecimal.ROUND_HALF_UP);
                    BigDecimal divide =
                            item.getLastEstValue().divide(lastFundNav.getJjjz(), 8, BigDecimal.ROUND_HALF_UP);
                    return foundIncrease.multiply(divide).subtract(new BigDecimal(1));
                } catch (Exception ex) {
                    System.out.println(
                            "获取净值有问题：" + item.getFund_id() + "+++" + JSON.toJSONString(fundNavModels));
                }
            default:
                if (fundNavModels.size() < 20) {
                    return new BigDecimal(-1);
                }
                FundNavModel firstDayNav = fundNavModels.get(18);
                FundNavModel lastFundNav = fundNavModels.get(0);
                BigDecimal foundIncrease =
                        lastFundNav.getLjjz().divide(firstDayNav.getLjjz(), 8, BigDecimal.ROUND_HALF_UP);
                BigDecimal divide = item.getLastEstValue().divide(lastFundNav.getJjjz(), 8, BigDecimal.ROUND_HALF_UP);
                return foundIncrease.multiply(divide).subtract(new BigDecimal(1));
        }
    }

    public static List<FundNavModel> loadFundNav(String fundId) {
        // 这里以新浪为例，实际可根据需要切换数据源
        return loadTwentyDayFundNavForSiNa(fundId);
    }

    public static List<FundNavModel> loadTwentyDayFundNavForSiNa(String fundId) {
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

    public static List<FundNavModel> loadTwentyDayFundNavForAjj(String fundId) {
        // 示例：实际实现可根据AJJ数据源格式调整
        return new ArrayList<>();
    }

    public static List<FundNavModel> loadTwentyDayFundNavForEtf(String fundId) {
        // 示例：实际实现可根据ETF数据源格式调整
        return new ArrayList<>();
    }

    public static List<JSONObject> getFundNavInfo() {
        List<JSONObject> fundNavInfo = new ArrayList<>();
        fundNavInfo.addAll(getFundNavInfo("1"));
        fundNavInfo.addAll(getFundNavInfo("2"));
        return fundNavInfo;
    }

    public static List<JSONObject> getFundNavInfo(String type) {
        String filePath = "D:\\Projects\\data\\xiaoyudecqg_tokens.txt";
        try {
            String token = FileUtil.readUtf8String(filePath);
            if (StringUtils.isBlank(token)) {
                throw new RuntimeException("token is null");
            }
            HttpRequest request = HttpUtil.createRequest(Method.GET,
                    "http://xiaoyudecqg.cn/htl/mp/api/arbitrage/list?type=" + type);
            request.header("token", token);
            String body = request.execute().body();
            JSONObject result = JSON.parseObject(body, JSONObject.class);
            if (CollUtil.isEmpty(result) || !"200".equalsIgnoreCase(result.getString("code"))) {
                return new ArrayList<>();
            }
            return result.getJSONObject("data").getJSONArray("arbitrageListVos")
                    .stream().map(u -> (JSONObject) u).collect(Collectors.toList());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private static String getAddCode(String code) {
        String substring = code.substring(0, 1);
        if (substring.equalsIgnoreCase("1")) {
            return "sz" + code;
        } else {
            return "sh" + code;
        }
    }

    private Map<String, BigDecimal> calXiaoYuIndexIncreate(List<JSONObject> fundNavInfo, List<FundOnModel> collect) {
        List<FundOnModel> collect1 = collect.stream()
                .filter(u -> u.getType().equalsIgnoreCase("qdiiC") || u.getType().equalsIgnoreCase("qdiiE"))
                .collect(Collectors.toList());
        Map<String, BigDecimal> inMap = new HashMap<>();
        fundNavInfo.forEach(item -> {
            Optional<FundOnModel> fundCode = collect1.stream()
                    .filter(u -> u.getFund_id().equalsIgnoreCase(item.getString("fundCode")))
                    .findFirst();
            if (!fundCode.isPresent()) {
                return;
            }
            FundOnModel fundOnModel = fundCode.get();
            BigDecimal value = item.getBigDecimal("value");
            BigDecimal divide = value.divide(new BigDecimal(fundOnModel.getFund_nav()), 8, BigDecimal.ROUND_HALF_UP);
            inMap.put(fundOnModel.getIndex_nm(), divide);
        });
        return inMap;
    }

    private List<FundOnModel> addScore(List<FundOnModel> collect, Comparator<FundOnModel> comparing) {
        collect = collect.stream().sorted(comparing).collect(Collectors.toList());
        for (int i = 0; i < collect.size(); i++) {
            FundOnModel fundOnModel = collect.get(i);
            fundOnModel.setAllScore(fundOnModel.getAllScore() + i + 1);
        }
        return collect;
    }

    private void insertAll(FundOnModel item) {
        if (item.getType().equalsIgnoreCase("cbBond")) {
            item.setPrice(item.getTrade_price());
        }
        if (StringUtils.isBlank(item.getFund_company())) {
            item.setFund_company(item.getIssuer_nm());
        }
    }
} 