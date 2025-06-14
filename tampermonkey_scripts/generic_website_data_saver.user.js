// ==UserScript==
// @name         通用网站数据与Cookie保存到本地系统
// @namespace    http://tampermonkey.net/
// @version      0.3
// @description  访问指定网站时，将页面域名、Cookie和数据发送到本地Spring Boot通用接口
// @author       Your Name
// @match        https://xueqiu.com/*
// @match        https://www.ycyflh.com/*
// @grant        GM_xmlhttpRequest
// @connect      localhost
// ==/UserScript==

(function() {
    'use strict';

    // 你的本地 Spring Boot 应用的通用数据接收接口地址
    const LOCAL_API_URL = 'http://localhost:8080/api/data/receive';

    // 获取当前页面的域名
    function getDomainFromUrl(url) {
        try {
            const urlObj = new URL(url);
            return urlObj.hostname;
        } catch (e) {
            console.error("获取域名失败:", e);
            return null;
        }
    }

    // 尝试获取页面所有可用的Cookie
    // 注意：document.cookie 只能获取到当前域名的非 HttpOnly Cookie
    function getPageCookies() {
        return document.cookie;
    }

    // 页面加载完成后执行
    window.addEventListener('load', function() {
        console.log('Tampermonkey 脚本加载。');

        const currentDomain = getDomainFromUrl(window.location.href);
        const pageCookies = getPageCookies();

        if (!currentDomain) {
            console.error('无法获取当前域名，跳过数据发送。');
            return;
        }

        let payload = {             // 其他你想要发送的页面数据
            url: window.location.href,
            title: document.title,
            timestamp: new Date().toISOString()
        };

        // 根据不同的域名添加特定的数据抓取逻辑
        if (currentDomain.includes('xueqiu.com')) {
            payload.website = '雪球网';
            // 示例：获取一些雪球网特定数据
            // let stockNameElement = document.querySelector('.stock-name');
            // if (stockNameElement) {
            //     payload.stockName = stockNameElement.innerText;
            // }
        } else if (currentDomain.includes('ycyflh.com')) {
            payload.website = '云飞系统';
            // 示例：获取云飞系统特定数据
            // 假设你想要获取页面上某个特定区域的文本内容
            let contentElement = document.querySelector('.content');
            if (contentElement) {
                payload.pageContent = contentElement.innerText; // 获取整个.content区域的文本
            }
            // 或者更具体地获取某个表格数据
            // let tableElement = document.querySelector('.content table');
            // if (tableElement) {
            //     payload.tableData = tableElement.innerText; // 获取表格文本内容
            // }
        } else {
            payload.website = '未知网站';
        }

        // 收集你想要发送的数据，包含通用结构
        const requestData = {
            domain: currentDomain, // 网站域名
            cookies: pageCookies,  // 页面 Cookie
            payload: payload       // 其他你想要发送的页面数据
        };

        console.log('准备发送的数据到本地系统:', requestData);

        // 使用 GM_xmlhttpRequest 发送跨域请求
        GM_xmlhttpRequest({
            method: 'POST',
            url: LOCAL_API_URL,
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(requestData),
            onload: function(response) {
                console.log('数据发送成功，本地系统响应:', response.responseText);
            },
            onerror: function(response) {
                console.error('数据发送失败，错误信息:', response.status, response.responseText);
            }
        });
    });
})(); 