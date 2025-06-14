package com.example.invest.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.GuornStrategy;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GuornService {
    private static final String GUORN_API_URL = "https://guorn.com/user/strategy";
    private static final String USER_ID = "1625252";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    @Resource
    private CookieManager cookieManager;

    public List<GuornStrategy> getAllStrategies() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", USER_ID);
        params.put("order", "1");
        String url = buildUrl(GUORN_API_URL, params);
        return executeRequest(url, this::parseStrategies);
    }

    public Optional<GuornStrategy> getStrategyById(String id) {
        return getAllStrategies().stream()
                .filter(strategy -> strategy.getId().equals(id))
                .findFirst();
    }

    public Optional<GuornStrategy> createStrategy(GuornStrategy strategy) {
        return Optional.ofNullable(executePostRequest(GUORN_API_URL, strategy));
    }

    public Optional<GuornStrategy> updateStrategy(GuornStrategy strategy) {
        String url = GUORN_API_URL + "/" + strategy.getId();
        return Optional.ofNullable(executePutRequest(url, strategy));
    }

    public boolean deleteStrategy(String id) {
        String url = GUORN_API_URL + "/" + id;
        return executeDeleteRequest(url);
    }

    private <T> T executeRequest(String url, ResponseHandler<T> handler) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Cookie", getCookies())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                log.info("请求成功: {}", responseBody);
                return handler.handle(responseBody);
            } else {
                log.error("请求失败: {}", response.code());
                return null;
            }
        } catch (IOException e) {
            log.error("请求异常", e);
            return null;
        }
    }

    private GuornStrategy executePostRequest(String url, GuornStrategy strategy) {
        return executeRequest(url, body -> {
            RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, JSON.toJSONString(strategy));
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", getCookies())
                    .build();

            try (Response response = new OkHttpClient().newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return JSON.parseObject(response.body().string(), GuornStrategy.class);
                }
                return null;
            }
        });
    }

    private GuornStrategy executePutRequest(String url, GuornStrategy strategy) {
        return executeRequest(url, body -> {
            RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, JSON.toJSONString(strategy));
            Request request = new Request.Builder()
                    .url(url)
                    .put(requestBody)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", getCookies())
                    .build();

            try (Response response = new OkHttpClient().newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return JSON.parseObject(response.body().string(), GuornStrategy.class);
                }
                return null;
            }
        });
    }

    private boolean executeDeleteRequest(String url) {
        return executeRequest(url, body -> {
            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .addHeader("Accept", "application/json")
                    .addHeader("Cookie", getCookies())
                    .build();

            try (Response response = new OkHttpClient().newCall(request).execute()) {
                return response.isSuccessful();
            }
        });
    }

    private List<GuornStrategy> parseStrategies(String jsonResponse) {
        try {
            JSONObject jsonObject = JSON.parseObject(jsonResponse);
            JSONArray data = jsonObject.getJSONArray("data");
            return data != null ? data.toJavaList(GuornStrategy.class) : new ArrayList<>();
        } catch (Exception e) {
            log.error("解析策略列表异常", e);
            return new ArrayList<>();
        }
    }

    private String buildUrl(String baseUrl, Map<String, String> params) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?");
        params.forEach((key, value) -> url.append(key).append("=").append(value).append("&"));
        url.append("_=").append(System.currentTimeMillis());
        return url.toString();
    }

    private String getCookies() {
        String cookies = cookieManager.getCookies("guorn.com");
        if (cookies == null || cookies.isEmpty()) {
            log.warn("未找到果仁网站的 cookies");
            return "";
        }
        return cookies;
    }

    @FunctionalInterface
    private interface ResponseHandler<T> {
        T handle(String responseBody) throws IOException;
    }
} 