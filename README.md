# invest-java

一个基于 Spring Boot 的投资策略与数据监控平台，支持网页监控、果仁策略管理、自动推送等功能。

## 主要功能

- **网页监控**：定时抓取指定网页内容，支持手动触发和定时监控，支持 Cookie 管理。
- **果仁策略管理**：集成果仁网策略的查询、创建、更新、删除等操作。
- **推送通知**：支持钉钉、微信等多渠道推送。
- **云飞监控**：支持云飞相关监控与手动触发。
- **配置管理**：支持多种监控配置的增删改查。

## 快速开始

### 1. 克隆项目

```bash
git clone https://your.repo.url/invest-java.git
cd invest-java
```

### 2. 配置依赖

- JDK 8+
- Maven 3.6+
- Chrome 浏览器（如需使用 selenium 相关功能）

### 3. 安装依赖

```bash
mvn clean install
```

### 4. 运行项目

```bash
mvn spring-boot:run
```

### 5. 访问接口

默认端口：`http://localhost:8080`

## 主要接口说明

### 网页监控

- `GET /api/web-monitor/configs` 获取所有网页监控配置
- `POST /api/web-monitor/execute/{id}` 手动触发某个监控

### 果仁策略

- `GET /api/guorn/strategies` 获取所有果仁策略
- `GET /api/guorn/strategies/{id}` 获取单个策略详情
- `POST /api/guorn/strategies` 新建策略
- `PUT /api/guorn/strategies/{id}` 更新策略
- `DELETE /api/guorn/strategies/{id}` 删除策略

### 云飞监控

- `POST /api/yun-monitor/execute/{id}` 手动触发云飞监控

## 依赖说明

- Spring Boot
- OkHttp
- Selenium（可选，部分功能需要）
- Fastjson
- Lombok

## 配置文件

- `config/web_monitor.json` 网页监控配置
- `config/system_config.json` 系统配置
- `config/yun_monitor.json` 云飞监控配置

## 常用命令

- 编译项目：`mvn clean compile`
- 运行测试：`mvn test`
- 启动服务：`mvn spring-boot:run`

## 贡献方式

1. Fork 本仓库
2. 新建分支：`git checkout -b feature/xxx`
3. 提交更改：`git commit -am 'feat: 新功能说明'`
4. 推送分支：`git push origin feature/xxx`
5. 提交 Pull Request

## License

MIT

---

如需详细接口文档或有其他问题，请联系项目维护者。
