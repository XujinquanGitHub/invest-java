@echo off
chcp 65001
cd /d %~dp0

:: 设置Java内存参数
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m

:: 设置日志目录
set LOG_DIR=..\logs
if not exist %LOG_DIR% mkdir %LOG_DIR%

:: 启动应用
java %JAVA_OPTS% -jar D:\Projects\MyProjects\invest-java\invest\target\invest-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --logging.file.path=%LOG_DIR% --logging.file.name=%LOG_DIR%/invest.log

:: 如果启动失败，等待用户查看错误信息
if errorlevel 1 (
    echo 应用程序启动失败，请查看日志文件：%LOG_DIR%\invest.log
    pause
) 