@echo off
chcp 65001
set "SCRIPT_PATH=%~dp0start-invest.bat"
reg add "HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Run" /v "InvestApp" /t REG_SZ /d "\"%SCRIPT_PATH%\"" /f
if %errorlevel%==0 (
    echo [成功] 注册表自启动项已写入！
) else (
    echo [失败] 请用“以管理员身份运行”本脚本！
)
pause