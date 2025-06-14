@echo off
cd /d %~dp0\..

echo 正在重启应用程序...

:: 调用停止脚本
call scripts\stop-invest.bat

:: 等待5秒
timeout /t 5 /nobreak

:: 调用启动脚本
call scripts\start-invest.bat 