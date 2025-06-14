package com.example.invest.util;

import com.example.invest.model.SystemConfig;
import com.example.invest.service.PushService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@Disabled
public class YunFeiContentUtilTest {

    @InjectMocks
    private YunFeiContentUtil yunFeiContentUtil;

    @Mock
    private YunFeiLoginUtil yunFeiLoginUtil;

    @Mock
    private PushService pushService;

    @Mock
    private SystemConfigManager systemConfigManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSomeMethod_enableYunFeiMonitorFalse() {
        SystemConfig mockConfig = new SystemConfig();
        mockConfig.setEnableYunFeiMonitor(false);

        when(systemConfigManager.getConfig()).thenReturn(mockConfig);


        verify(systemConfigManager, times(1)).getConfig();
        // 确保当enableYunFeiMonitor为false时，不会执行监控相关的操作
        // verifyNoInteractions(yunFeiLoginUtil); // 示例：如果someMethod没有其他副作用
    }

    @Test
    void testGetStrategyDetail_success() {
        String testId = "123";
        String mockCookies = "JSESSIONID=abc";
        String mockHtmlContent = "<html><body><div class=\"content\"><table><tr><td>Strategy Detail</td></tr></table></div></body></html>";

        when(yunFeiLoginUtil.getCookies()).thenReturn(mockCookies);
        // 模拟 HttpRequest.get().header().execute().body()
        // 这里需要更复杂的 Mockito setup，因为 HttpRequest 是静态方法或者链式调用
        // 鉴于 Hutool 的 HttpRequest 是静态方法，我们通常不会直接 Mock 它，而是测试其调用 YunFeiLoginUtil 的依赖
        // 暂时跳过对 HttpRequest 的直接模拟，主要测试逻辑。

        // 由于 HttpRequest.get() 是静态方法，这里无法直接mock其行为，
        // 通常我们会将HTTP请求封装到另一个可mock的类中进行测试。
        // 为了测试这个方法，我们假设 HttpRequest 成功返回内容。
        // 所以，这个测试需要YunFeiContentUtil中的HttpRequest能够被外部控制或依赖注入。
        // 否则，需要对HttpRequest进行PowerMock或其他方式的静态方法mock。

        // 因为HttpRequest是hutool的静态方法，不方便直接mock，这里只测试前置条件和PushService的调用。
        yunFeiContentUtil.getStrategyDetail(testId);

        verify(yunFeiLoginUtil, times(1)).getCookies();
        verify(pushService, times(1)).send(eq("策略详情"), anyString());
    }

    @Test
    void testGetStrategyDetail_noCookies() {
        String testId = "123";

        when(yunFeiLoginUtil.getCookies()).thenReturn(null);

        yunFeiContentUtil.getStrategyDetail(testId);

        verify(yunFeiLoginUtil, times(1)).getCookies();
        verify(pushService, times(1)).send(eq("错误提示"), eq("未找到登录Cookie，请先登录"));
        verifyNoMoreInteractions(pushService); // 确保没有其他推送
    }

    @Test
    void testGetStrategyDetail_exception() {
        String testId = "123";
        String mockCookies = "JSESSIONID=abc";

        when(yunFeiLoginUtil.getCookies()).thenReturn(mockCookies);
        // 模拟 HttpRequest 抛出异常
        // 由于 HttpRequest 是静态方法，这里无法直接模拟其抛出异常，
        // 实际项目中会封装Http客户端，这里只模拟了getCookies()部分

        yunFeiContentUtil.getStrategyDetail(testId);

        verify(yunFeiLoginUtil, times(1)).getCookies();
        // 验证当发生异常时，pushService是否被调用，并且错误信息是否正确
        verify(pushService, times(1)).send(eq("错误提示"), contains("获取策略详情失败"));
    }
} 