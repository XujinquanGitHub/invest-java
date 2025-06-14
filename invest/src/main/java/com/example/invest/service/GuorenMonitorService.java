package com.example.invest.service;

import com.example.invest.model.GuorenConfig;
import com.example.invest.model.SystemConfig;
import com.example.invest.util.SystemConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Slf4j
@Service
public class GuorenMonitorService {

    @Resource
    private SystemConfigManager systemConfigManager;

    @Resource
    private PushService pushService; // 注入 PushService 以便发送通知

    @Scheduled(fixedDelay = 60000) // 每分钟检查一次
    public void scheduledGuorenMonitorTask() {
        SystemConfig config = systemConfigManager.getConfig();
        if (!config.isEnableGuorenMonitor()) {
            //log.info("果仁网监控已禁用，跳过检查。"); // 避免频繁日志
            return;
        }

        List<GuorenConfig> guorenConfigs = config.getGuorenConfigs();
        if (guorenConfigs == null || guorenConfigs.isEmpty()) {
            //log.warn("果仁网账号配置为空，跳过检查。"); // 避免频繁日志
            return;
        }

        LocalTime now = LocalTime.now();
        DayOfWeek today = DayOfWeek.from(now); // 获取今天是星期几
        int currentDayOfWeek = today.getValue(); // 1 (Monday) to 7 (Sunday)

        for (GuorenConfig guorenConfig : guorenConfigs) {
            // 检查账号是否启用
            if (!guorenConfig.isEnabled()) {
                continue;
            }

            // 检查当前星期是否在监控范围内
            String monitorWeekdaysStr = guorenConfig.getMonitorWeekdays();
            if (monitorWeekdaysStr != null && !monitorWeekdaysStr.isEmpty()) {
                List<Integer> monitorWeekdays = Arrays.stream(monitorWeekdaysStr.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                if (!monitorWeekdays.contains(currentDayOfWeek)) {
                    continue; // 今天不需要监控此账号
                }
            }

            // 检查当前时间是否在监控时间点内
            String monitorTimesStr = guorenConfig.getMonitorTimes();
            if (monitorTimesStr == null || monitorTimesStr.isEmpty()) {
                log.warn("果仁网账号 [{}] 监控时间点未配置，跳过检查。");
                continue;
            }

            boolean shouldExecute = Arrays.stream(monitorTimesStr.split(","))
                    .anyMatch(timeStr -> {
                        try {
                            LocalTime scheduledTime = LocalTime.parse(timeStr.trim());
                            // 检查当前时间是否在计划时间的一分钟内
                            return now.isAfter(scheduledTime.minusMinutes(1)) && now.isBefore(scheduledTime.plusMinutes(1));
                        } catch (Exception e) {
                            log.error("解析果仁网监控时间点失败: {} - {}", guorenConfig.getUsername(), timeStr, e);
                            return false;
                        }
                    });

            if (shouldExecute) {
                log.info("执行果仁网账号 [{}] 监控任务...", guorenConfig.getUsername());
                // TODO: 在这里添加实际的果仁网监控逻辑
                // 例如：调用果仁网API，获取数据，判断是否需要推送通知
                pushService.send("果仁网监控通知", String.format("果仁网账号 [%s] 达到监控时间点，执行监控。", guorenConfig.getUsername()));
            }
        }
    }
} 