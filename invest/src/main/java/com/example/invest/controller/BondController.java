package com.example.invest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.modules.bond.utils.BondHelper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bond")
public class BondController {

    @PostMapping("/allBond")
    public List<JSONObject> allBond(@RequestBody Map<String, BigDecimal> para) {
        return BondHelper.bondList(para);
    }

    @PostMapping("/allBond1")
    public List<JSONObject> allBond1(@RequestBody String para) {
        try {
            String decode = URLDecoder.decode(para, StandardCharsets.UTF_8.name());
            String substring = decode.substring(0, decode.length() - 1);
            JSONArray data = JSON.parseObject(substring, JSONObject.class).getJSONArray("para");
            Map<String, BigDecimal> collect = data.stream().map(u -> (JSONObject) u)
                    .collect(Collectors.toMap(u -> u.getString("id"), u -> u.getBigDecimal("p")));
            return BondHelper.bondList(collect);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 