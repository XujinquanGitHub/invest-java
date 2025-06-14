package com.example.invest.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CookieManager {
    private final Map<String, String> cookieStore = new ConcurrentHashMap<>();

    public void setCookies(String domain, String cookies) {
        cookieStore.put(domain, cookies);
    }

    public String getCookies(String domain) {
        return cookieStore.get(domain);
    }

    public void removeCookies(String domain) {
        cookieStore.remove(domain);
    }

    public void clearCookies() {
        cookieStore.clear();
    }
} 