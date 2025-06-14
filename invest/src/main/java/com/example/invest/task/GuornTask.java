package com.example.invest.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.invest.model.GuorenConfig;
import com.example.invest.service.CookieManager;
import com.example.invest.service.PushService;
import com.example.invest.util.ChromeDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GuornTask {
    private static Set<String> loadNameSet = new HashSet<>();

    @Resource
    private CookieManager cookieManager;

    @Resource
    private PushService pushService;

    @Scheduled(cron = "0 0 6 * * ?")
    public void clearData() {
        loadNameSet = new HashSet<>();
    }

    @Scheduled(cron = "*/12 */6 7,8,9,19,20,21 * * ?")
    public void execute() {
        if (!isBusinessDay()) {
            return;
        }

        List<GuorenConfig> configs = getGuorenConfigs();
        if (CollUtil.isEmpty(configs)) {
            return;
        }

        for (GuorenConfig config : configs) {
            if (!config.isEnabled()) {
                continue;
            }

            try {
                processGuorenConfig(config);
            } catch (Exception e) {
                log.error("处理果仁配置异常: {}", config.getUsername(), e);
                pushService.pushError("果仁网监控异常", e.getMessage());
            }
        }
    }

    private boolean isBusinessDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY;
    }

    private List<GuorenConfig> getGuorenConfigs() {
        // TODO: 从配置中获取果仁网账号配置
        return new ArrayList<>();
    }

    private void processGuorenConfig(GuorenConfig config) {
        WebDriver driver = ChromeDriverManager.createChromeDriver();
        try {
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

            // 登录果仁网
            loginGuoren(driver, config);

            // 获取策略列表
            List<String> strategyIds = Arrays.asList(config.getStrategyIds().split(","));
            for (String strategyId : strategyIds) {
                processStrategy(driver, strategyId);
            }
        } finally {
            driver.quit();
        }
    }

    private void loginGuoren(WebDriver driver, GuorenConfig config) {
        driver.get("https://guorn.com/");
        WebElement login = driver.findElement(By.className("login")).findElement(By.tagName("a"));
        login.click();
        WebElement nameText = driver.findElement(By.id("login-Id"));
        nameText.sendKeys(config.getUsername());
        WebElement passText = driver.findElement(By.id("login-password"));
        passText.sendKeys(config.getPassword());
        driver.findElement(By.id("sign-in")).click();

        // 保存cookies
        Set<org.openqa.selenium.Cookie> cookies = driver.manage().getCookies();
        StringBuilder cookieStr = new StringBuilder();
        for (org.openqa.selenium.Cookie cookie : cookies) {
            cookieStr.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }
        cookieManager.setCookies("guorn.com", cookieStr.toString());
    }

    private void processStrategy(WebDriver driver, String strategyId) {
        driver.get("https://guorn.com/strategy/" + strategyId);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement enter = driver.findElement(By.className("enter"));
        enter.click();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        driver.findElements(By.className("screen")).stream()
                .filter(u -> u.getText().equalsIgnoreCase("每日选股"))
                .findFirst()
                .ifPresent(WebElement::click);

        driver.findElement(By.id("screen-submit")).click();

        // 获取选股结果
        String number = driver.findElement(By.id("setting-trading-cnt"))
                .findElement(By.className("stock-num"))
                .findElement(By.className("number"))
                .getAttribute("value");

        int len = 20;
        if (NumberUtil.isNumber(number)) {
            len = Integer.parseInt(number);
        }

        List<WebElement> elements = driver.findElement(By.id("screen-grid"))
                .findElement(By.className("body"))
                .findElement(By.className("tt"))
                .findElements(By.className("tr"));

        List<Map<String, String>> stockList = new ArrayList<>();
        for (WebElement element : elements) {
            Map<String, String> stock = new HashMap<>();
            List<WebElement> tds = element.findElements(By.tagName("td"));
            stock.put("code", tds.get(0).getText());
            stock.put("name", tds.get(1).getText());
            stock.put("price", tds.get(2).getText());
            stock.put("change", tds.get(3).getText());
            stockList.add(stock);
        }

        // 推送选股结果
        if (!stockList.isEmpty()) {
            String message = buildStockMessage(stockList);
            pushService.pushMessage("果仁网选股结果", message);
        }
    }

    private String buildStockMessage(List<Map<String, String>> stockList) {
        StringBuilder sb = new StringBuilder();
        sb.append("今日选股结果：\n");
        for (Map<String, String> stock : stockList) {
            sb.append(String.format("%s(%s) 现价:%s 涨跌幅:%s\n",
                    stock.get("name"),
                    stock.get("code"),
                    stock.get("price"),
                    stock.get("change")));
        }
        return sb.toString();
    }
} 