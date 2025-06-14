package com.example.invest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class CookieManager {

    private static final String COOKIE_BASE_DIR = "D:\\Projects\\data\\cookies";

    public CookieManager() {
        // 确保 Cookie 存储目录存在
        File dir = new File(COOKIE_BASE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            log.info("创建 Cookie 存储目录: {}", COOKIE_BASE_DIR);
        }
    }

    /**
     * 根据域名保存 Cookie
     * @param domain 网站域名 (例如: xueqiu.com)
     * @param cookies 要保存的 Cookie 字符串
     */
    public void saveCookies(String domain, String cookies) {
        String filePath = getCookieFilePath(domain);
        try {
            Files.write(Paths.get(filePath), cookies.getBytes(StandardCharsets.UTF_8));
            log.info("成功保存 {} 的 Cookie 到: {}", domain, filePath);
        } catch (IOException e) {
            log.error("保存 {} 的 Cookie 失败: {}", domain, e.getMessage());
        }
    }

    /**
     * 根据域名获取 Cookie
     * @param domain 网站域名
     * @return Cookie 字符串，如果不存在则返回 null
     */
    public String getCookies(String domain) {
        String filePath = getCookieFilePath(domain);
        File cookieFile = new File(filePath);
        if (!cookieFile.exists()) {
            log.warn("未找到 {} 的 Cookie 文件: {}", domain, filePath);
            return null;
        }
        try {
            String cookies = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            log.info("成功读取 {} 的 Cookie 从: {}", domain, filePath);
            return cookies;
        } catch (IOException e) {
            log.error("读取 {} 的 Cookie 失败: {}", domain, e.getMessage());
            return null;
        }
    }

    private String getCookieFilePath(String domain) {
        // 使用域名作为文件名，确保文件名安全
        String safeDomain = domain.replaceAll("[^a-zA-Z0-9.-]", "_");
        return Paths.get(COOKIE_BASE_DIR, safeDomain + "_cookies.txt").toString();
    }
} 