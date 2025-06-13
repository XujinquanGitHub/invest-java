package com.example.invest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    
    @GetMapping("/")
    public String index() {
        return "redirect:/system-config";
    }
    
    @GetMapping("/system-config")
    public String systemConfig() {
        return "system-config";
    }
    
    @GetMapping("/web-monitor-config")
    public String webMonitorConfig() {
        return "web-monitor-config";
    }
} 