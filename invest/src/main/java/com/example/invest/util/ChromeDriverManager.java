package com.example.invest.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ChromeDriverManager {
    private static final String CHROME_DRIVER_VERSION = "114.0.5735.90"; // 与Chrome浏览器版本对应
    private static final String CHROME_DRIVER_URL = "https://chromedriver.storage.googleapis.com/" + CHROME_DRIVER_VERSION + "/chromedriver_win32.zip";
    private static final String CHROME_DRIVER_PATH = "drivers/chromedriver.exe";

    public static void setupChromeDriver() {
        try {
            // 创建drivers目录
            Path driverPath = Paths.get(CHROME_DRIVER_PATH);
            Files.createDirectories(driverPath.getParent());

            // 检查ChromeDriver是否已存在
            File driverFile = driverPath.toFile();
            if (!driverFile.exists()) {
                log.info("ChromeDriver不存在，开始下载...");
                downloadChromeDriver();
            }

            // 设置ChromeDriver路径
            System.setProperty("webdriver.chrome.driver", driverFile.getAbsolutePath());
            log.info("ChromeDriver设置完成: {}", driverFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("设置ChromeDriver失败", e);
            throw new RuntimeException("设置ChromeDriver失败", e);
        }
    }

    private static void downloadChromeDriver() throws IOException {
        log.info("开始下载ChromeDriver: {}", CHROME_DRIVER_URL);
        
        // 下载zip文件
        File zipFile = new File("drivers/chromedriver.zip");
        FileUtils.copyURLToFile(new URL(CHROME_DRIVER_URL), zipFile);
        
        // 解压文件
        // TODO: 实现zip文件解压逻辑
        // 由于Java没有内置的zip解压功能，您可以使用以下方法之一：
        // 1. 使用commons-compress库
        // 2. 使用java.util.zip包
        // 3. 使用第三方库如zip4j
        
        // 删除zip文件
        zipFile.delete();
        
        log.info("ChromeDriver下载完成");
    }

    public static ChromeDriver createChromeDriver() {
        setupChromeDriver();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        return new ChromeDriver(options);
    }
} 