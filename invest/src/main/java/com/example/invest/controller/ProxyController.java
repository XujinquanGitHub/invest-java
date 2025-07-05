package com.example.invest.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.example.invest.job.FundTask;
import com.example.invest.model.YunFundModel;
import com.example.invest.model.JslFundModel;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final RestTemplate restTemplate;

    public ProxyController() {
        this.restTemplate = new RestTemplate();
    }

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE})
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

    @GetMapping(value = "/allLof")
    public JSONObject allLof(Map<String, BigDecimal> para) {
        List<JSONObject> fundNavInfo = FundTask.getFundNavInfo();
        List<JslFundModel> collect = fundNavInfo.stream()
                .map(u -> u.toJavaObject(YunFundModel.class))
                .map(JslFundModel::tran)
                .collect(Collectors.toList());
        List<JSONObject> result = collect.stream()
                .map(u -> new JSONObject().fluentPut("cell", u).fluentPut("id", u.getFund_id()))
                .collect(Collectors.toList());
        return new JSONObject().fluentPut("rows", result).fluentPut("page", 1);
    }

} 