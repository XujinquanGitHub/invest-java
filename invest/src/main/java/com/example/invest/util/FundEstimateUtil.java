package com.example.invest.util;

import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.FundOnModel;
import cn.hutool.core.collection.CollUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FundEstimateUtil {
    public static JSONObject getEstValue(FundOnModel item, List<JSONObject> qdIIList, Map<String, BigDecimal> inMap) {
        BigDecimal estValue = new BigDecimal(0);
        String estType = "";
        switch (item.getType()) {
            case "qdiiE":
                List<JSONObject> fundCode =
                        qdIIList.stream().filter(u -> u.getString("fundCode").equalsIgnoreCase(item.getFund_id()))
                                .collect(Collectors.toList());
                String ref_increase_rt = item.getRef_increase_rt();
                if (CollUtil.isEmpty(fundCode)) {
                    if (inMap.containsKey(item.getIndex_nm())) {
                        estValue = new BigDecimal(item.getFund_nav()).multiply(inMap.get(item.getIndex_nm()));
                        estType = "小鱼逆推";
                    } else {
                        String refIncrease = ref_increase_rt.replace("%", "");
                        if (StringUtils.isNotBlank(refIncrease) && !"-".equalsIgnoreCase(refIncrease)) {
                            BigDecimal refCre =
                                    new BigDecimal(refIncrease).divide(new BigDecimal(100), 8, BigDecimal.ROUND_HALF_UP)
                                            .add(new BigDecimal(1));
                            estValue = new BigDecimal(item.getEstimate_value()).multiply(refCre);
                            estType = "t1*ref";
                        } else {
                            System.out.println("错误：" + JSONObject.toJSONString(item));
                        }
                    }
                } else {
                    estValue = fundCode.get(0).getBigDecimal("value");
                    estType = "小鱼";
                }
                break;
            case "qdiiC":
                List<JSONObject> fundCodeC =
                        qdIIList.stream().filter(u -> u.getString("fundCode").equalsIgnoreCase(item.getFund_id()))
                                .collect(Collectors.toList());
                if (CollUtil.isEmpty(fundCodeC)) {
                    if (inMap.containsKey(item.getIndex_nm())) {
                        estValue = new BigDecimal(item.getFund_nav()).multiply(inMap.get(item.getIndex_nm()));
                        estType = "小鱼逆推";
                    } else {
                        if (StringUtils.isBlank(item.getEstimate_value()) ||
                                "-".equalsIgnoreCase(item.getEstimate_value())) {
                            estValue = null;
                            estType = "null";
                            break;
                        }
                        String increase = item.getIncrease_rt().replace("%", "");
                        BigDecimal refCre =
                                new BigDecimal(increase).divide(new BigDecimal(100), 8, BigDecimal.ROUND_HALF_UP)
                                        .add(new BigDecimal(1));
                        estValue = new BigDecimal(item.getEstimate_value()).multiply(refCre);
                        estType = "t1*price*cre";
                    }
                } else {
                    estValue = fundCodeC.get(0).getBigDecimal("value");
                    estType = "小鱼";
                }
                break;
            case "cbBond":
                estValue = new BigDecimal(item.getEst_val());
                estType = "jsl";
                break;
            case "cf":
                estValue = new BigDecimal(item.getRealtime_estimate_value());
                estType = "jsl";
                break;
            default:
                String est_val = item.getEstimate_value();
                if ("-".equalsIgnoreCase(est_val) || StringUtils.isBlank(est_val)) {
                    estValue = new BigDecimal(item.getPrice());
                    estType = "jslPrice";
                    break;
                }
                estValue = new BigDecimal(est_val);
                estType = "jsl";
                break;
        }
        return new JSONObject().fluentPut("estValue", estValue).fluentPut("estType", estType);
    }
} 