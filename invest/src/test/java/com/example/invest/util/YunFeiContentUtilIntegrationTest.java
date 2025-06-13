package com.example.invest.util;

import com.example.invest.model.SystemConfig;
import com.example.invest.service.PushService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 集成测试：测试 YunFeiContentUtil 与真实依赖的交互。
 * 请确保您的 config/system_config.json 中配置了有效的云飞系统登录凭据。
 * 运行此测试需要网络连接。
 */
@SpringBootTest
@Disabled
public class YunFeiContentUtilIntegrationTest {

    @Autowired
    private YunFeiContentUtil yunFeiContentUtil;

    @Autowired
    private YunFeiLoginUtil yunFeiLoginUtil;

    @Autowired
    private PushService pushService;

    @Autowired
    private SystemConfigManager systemConfigManager;

    @Test
    void testDependenciesAreInjected() {
        assertNotNull(yunFeiContentUtil);
        assertNotNull(yunFeiLoginUtil);
        assertNotNull(pushService);
        assertNotNull(systemConfigManager);
    }

    @Test
    void testSomeMethod_realConfigCheck() {
        SystemConfig config = systemConfigManager.getConfig();
        boolean initialEnableYunFeiMonitor = config.isEnableYunFeiMonitor();

        System.out.println("当前系统配置中 'enableYunFeiMonitor' 的值为: " + initialEnableYunFeiMonitor);

        yunFeiContentUtil.someMethod();
    }

    @Test
    void testGetStrategyDetail_realCall_successScenario() {
        SystemConfig config = systemConfigManager.getConfig();
        String username = config.getYunFeiUsername();
        String password = config.getYunFeiPassword();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.err.println("云飞系统登录凭据未在 config/system_config.json 中配置，跳过此测试。");
            assertTrue(false, "云飞系统登录凭据未配置。");
            return;
        }

        String cookies = yunFeiLoginUtil.login(username, password);
        assertNotNull(cookies, "真实登录失败，未获取到 Cookie。请检查用户名和密码。");
        System.out.println("真实登录成功，获取到 Cookie: " + cookies.substring(0, Math.min(cookies.length(), 50)) + "...");

        String testStrategyId = "请替换为真实的云飞策略ID";

        System.out.println("正在尝试获取策略详情，ID: " + testStrategyId);

        yunFeiContentUtil.getStrategyDetail(testStrategyId);

        System.out.println("已调用 getStrategyDetail，请检查控制台日志和可能的推送通知。");
    }

    @Test
    void testGetStrategyDetail_noCookies_realCall() {
        System.out.println("测试 getStrategyDetail 在没有 Cookie 情况下的真实调用。");
        yunFeiContentUtil.getStrategyDetail("any_id");

        System.out.println("已调用 getStrategyDetail，请检查控制台日志。");
    }
} 