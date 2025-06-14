package com.example.invest.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 云飞系统登录工具类
 */
@Component
public class YunFeiLoginUtil {

    @Resource
    private CookieManager cookieManager;

    private static final String BASE_URL = "https://www.ycyflh.com";

    /**
     * 云飞系统登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功后的Cookie
     */
    public String login(String username, String password) {
        String url = BASE_URL + "/F2/login.aspx";

        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "www.ycyflh.com");
        headers.put("Connection", "keep-alive");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
        headers.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8");
        headers.put("Origin", BASE_URL);
        headers.put("Referer", url);

        // 构建表单数据
        Map<String, Object> formMap = new HashMap<>();
        formMap.put("__VIEWSTATE", "/wEPDwUKMTMxMjU3MzkyNWRkEp/qO63ekv7bMVvU8CscJ6WkWFOvQ9lI23mLqQgzEl8=");
        formMap.put("__VIEWSTATEGENERATOR", "08B844BE");
        formMap.put("__EVENTVALIDATION",
                "/wEdAASHu/AagveqK95rnmcNoyh1zCVMJGnCxB7ZG7bkLlekEsa8wzphH9jstV6K9RvF+kh0OWarNtHZvueomBCnXS0aJnehRfEWK+fFgD1yJv1o0Z7UrAidTWQaJDTtweYIOKc=");
        formMap.put("txt_name_2020_byf", username);
        formMap.put("txt_pwd_2020_byf", password);
        formMap.put("btn_login", "登 录");
        formMap.put("ckb_UserAgreement", "on");

        // 发送POST请求
        HttpResponse response = HttpRequest.post(url)
                .headerMap(headers, true)
                .form(formMap)
                .execute();

        // 获取响应头中的Cookie
        List<String> cookies = response.headers().get("Set-Cookie");

        // 保存Cookie到文件
        if (CollUtil.isNotEmpty(cookies)) {
            String collect = String.join(";", cookies);
            cookieManager.saveCookies(BASE_URL, collect);
            return collect;
        }

        return "";
    }

    /**
     * 获取已保存的Cookie
     *
     * @return Cookie字符串
     */
    public String getCookies() {
        return cookieManager.getCookies(BASE_URL);
    }
} 