package com.example.invest.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final RestTemplate restTemplate;

    public ProxyController() {
        this.restTemplate = new RestTemplate();
    }

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> proxyRequest(
            @RequestParam(required = false) String targetUrl,
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) {
        
        if (targetUrl == null || targetUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("目标URL不能为空");
        }

        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (!headerName.equalsIgnoreCase("host") && !headerName.equalsIgnoreCase("content-length")) {
                    headers.set(headerName, request.getHeader(headerName));
                }
            }

            // 构建请求参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(targetUrl);
            parameterMap.forEach((key, values) -> {
                if (!key.equals("targetUrl")) {
                    for (String value : values) {
                        builder.queryParam(key, value);
                    }
                }
            });

            // 创建请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    method,
                    requestEntity,
                    String.class
            );

            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("代理请求失败: " + e.getMessage());
        }
    }

//
//    @RequestMapping(value = "/req", method = RequestMethod.POST)
//    public String req(ProxyModel request) {
//        Method method=Method.GET;
//        if ("post".equalsIgnoreCase(request.getMethod())){
//            method=Method.POST;
//        }
//        String url = request.getUrl();
//        if (method.equals(Method.GET)){
//            return HttpUtil.get(url);
//        }else {
//            return HttpUtil.post(url,request.getData());
//        }
//    }

//    @RequestMapping(value = "/addCookie", method = RequestMethod.POST)
//    public String addCookie(CookiesModel cookie) {
//        return CookiesUtil.saveCookie(cookie);
//    }
//
//    @Autowired
//    private FundTask fundTask;

//    @RequestMapping(value = "/allFund", method = RequestMethod.POST)
//    public JSONObject allFund(String list) {
//        List<FundOnModel> sous = fundTask.sous(list);
//        List<JSONObject> data = sous.stream()
//                .map(u -> new JSONObject().fluentPut("cell", u).fluentPut("id", u.getFund_id()))
//                .collect(
//                        Collectors.toList());
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.fluentPut("rows",data);
//        jsonObject.fluentPut("page",1);
//        return jsonObject;
//    }

//    @GetMapping(value = "/allFundXY")
//    public JSONObject allFundXY(String list) {
//        List<FundOnModel> sous = fundTask.sous(list);
//        List<JSONObject> data = sous.stream()
//                .map(u -> new JSONObject().fluentPut("cell", u).fluentPut("id", u.getFund_id()))
//                .collect(
//                        Collectors.toList());
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.fluentPut("rows",data);
//        jsonObject.fluentPut("page",1);
//        return jsonObject;
//    }
//
//    @RequestMapping(value = "/allBond", method = RequestMethod.POST)
//    public List<JSONObject>  allBond(Map<String, BigDecimal> para) {
//        return BondHelper.bondList(para);
//    }
//
//    @RequestMapping(value = "/allBond1", method = RequestMethod.POST)
//    public List<JSONObject>  allBond1(@RequestBody String para) {
//        String decode = URLDecoder.decode(para);
//        String substring = decode.substring(0, decode.length() - 1);
//        JSONArray data = JSON.parseObject(substring, JSONObject.class).getJSONArray("para");
//        Map<String, BigDecimal> collect = data.stream().map(u -> (JSONObject) u)
//                .collect(Collectors.toMap(u -> u.getString("id"), u -> u.getBigDecimal("p")));
//        return BondHelper.bondList(collect);
//    }

//
//    @GetMapping(value = "/allLof")
//    public JSONObject  allLof(Map<String, BigDecimal> para) {
//
//        List<JSONObject> fundNavInfo = FundTask.getFundNavInfo();
//
//        List<JSONObject> collect = fundNavInfo.stream().map(u -> u.toJavaObject(YunFundModel.class)).map(u -> JslFundModel.tran(u))
//                .map(u -> new JSONObject().fluentPut("cell", u).fluentPut("id", u.getFund_id()))
//                .collect(Collectors.toList());
//
//        return new JSONObject().fluentPut("rows",collect).fluentPut("page",1);
//    }


} 