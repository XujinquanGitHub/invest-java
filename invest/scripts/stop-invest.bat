@echo off
cd /d %~dp0\..

:: 查找Java进程
for /f "tokens=1" %%a in ('jps -l ^| findstr "invest-0.0.1-SNAPSHOT.jar"') do (
    echo 正在停止进程: %%a
    taskkill /F /PID %%a
)

:: 等待进程完全停止
timeout /t 5 /nobreak

:: 检查是否还有进程在运行
jps -l | findstr "invest-0.0.1-SNAPSHOT.jar"
if errorlevel 1 (
    echo 应用程序已成功停止
) else (
    echo 警告：应用程序可能未完全停止
)
pause 