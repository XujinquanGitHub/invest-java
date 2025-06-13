package com.example.invest.util;

import org.springframework.stereotype.Component;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class CookieManager {
    private static final String COOKIE_DIR = "D:\\Projects\\data\\cookies";
    
    /**
     * 保存Cookie到文件
     * @param domain 域名
     * @param cookies Cookie字符串
     */
    public void saveCookies(String domain, String cookies) {
        try {
            // 创建cookies目录
            Path cookieDir = Paths.get(COOKIE_DIR);
            if (!Files.exists(cookieDir)) {
                Files.createDirectories(cookieDir);
            }
            
            // 获取域名作为文件名
            String fileName = getDomainName(domain);
            Path cookieFile = cookieDir.resolve(fileName + ".txt");
            
            // 写入Cookie
            Files.write(cookieFile, cookies.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 从文件读取Cookie
     * @param domain 域名
     * @return Cookie字符串
     */
    public String loadCookies(String domain) {
        try {
            String fileName = getDomainName(domain);
            Path cookieFile = Paths.get(COOKIE_DIR, fileName + ".txt");
            
            if (Files.exists(cookieFile)) {
                return new String(Files.readAllBytes(cookieFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取域名
     * @param url 完整URL
     * @return 域名
     */
    private String getDomainName(String url) {
        try {
            URL urlObj = new URL(url);
            String domain = urlObj.getHost();
            return domain.replace(".", "_");
        } catch (Exception e) {
            return url.replace(".", "_");
        }
    }
} 