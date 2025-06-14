@echo off
chcp 65001
cd /d %~dp0\..

echo 检查应用程序状态...

:: 检查Java进程
jps -l | findstr "invest-0.0.1-SNAPSHOT.jar"
if errorlevel 1 (
    echo 应用程序未运行
) else (
    echo 应用程序正在运行
)

:: 检查日志文件
if exist logs\invest.log (
    echo 最后10行日志内容：
    type logs\invest.log | findstr /n "^" | findstr /b "[0-9]*:" | tail -10
) else (
    echo 未找到日志文件
)

pause 