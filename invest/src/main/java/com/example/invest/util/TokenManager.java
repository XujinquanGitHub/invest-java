package com.example.invest.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenManager {
    private static final String TOKEN_DIR = "D:/Projects/data/cookies/";
    private static final Map<String, String> tokenCache = new HashMap<>();

    static {
        loadTokens();
    }

    private static void loadTokens() {
        try {
            String tokenPath = TOKEN_DIR + "tushare_token.txt";
            log.info("正在从文件加载 Tushare token: {}", tokenPath);
            
            if (!Files.exists(Paths.get(tokenPath))) {
                log.error("Tushare token 文件不存在: {}", tokenPath);
                return;
            }

            String tushareToken = new String(Files.readAllBytes(Paths.get(tokenPath))).trim();
            if (tushareToken.isEmpty()) {
                log.error("Tushare token 文件内容为空");
                return;
            }

            tokenCache.put("tushare", tushareToken);
            log.info("Tushare token 加载成功，长度: {}", tushareToken.length());
        } catch (IOException e) {
            log.error("加载 token 文件失败", e);
        }
    }

    public static String getTushareToken() {
        String token = tokenCache.getOrDefault("tushare", "");
        if (token.isEmpty()) {
            log.warn("Tushare token 未加载或为空");
        }
        return token;
    }

    public static void reloadTokens() {
        tokenCache.clear();
        try {
            String tokenPath = TOKEN_DIR + "tushare_token.txt";
            log.info("正在重新加载 Tushare token: {}", tokenPath);
            
            if (!Files.exists(Paths.get(tokenPath))) {
                log.error("Tushare token 文件不存在: {}", tokenPath);
                return;
            }

            String tushareToken = new String(Files.readAllBytes(Paths.get(tokenPath))).trim();
            if (tushareToken.isEmpty()) {
                log.error("Tushare token 文件内容为空");
                return;
            }

            tokenCache.put("tushare", tushareToken);
            log.info("Tushare token 重新加载成功，长度: {}", tushareToken.length());
        } catch (IOException e) {
            log.error("重新加载 token 文件失败", e);
        }
    }
} 