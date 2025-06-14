@echo off
chcp 65001
cd /d %~dp0\..

echo 检查自启动配置状态...

echo.
echo 1. 检查注册表启动项：
reg query "HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Run" /v "InvestApp"
if errorlevel 1 (
    echo [未找到] 注册表启动项未配置
) else (
    echo [已配置] 注册表启动项已配置
)

echo.
echo 2. 检查任务计划：
schtasks /query /tn "InvestAppStartup" /fo list
if errorlevel 1 (
    echo [未找到] 任务计划未配置
) else (
    echo [已配置] 任务计划已配置
)

echo.
echo 3. 检查启动脚本是否存在：
if exist "%~dp0start-invest.bat" (
    echo [正常] 启动脚本存在
) else (
    echo [错误] 启动脚本不存在
)

echo.
echo 4. 检查Java环境：
java -version
if errorlevel 1 (
    echo [错误] Java环境未配置
) else (
    echo [正常] Java环境已配置
)

echo.
echo 检查完成！
pause 