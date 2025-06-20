package com.example.invest.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSON;
import com.example.invest.model.CookiesModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public class CookiesUtil {
    public static String saveCookie(String name, String value) {
        return saveCookie(new CookiesModel().setCookies(value).setWebName(name));
    }
    public static String saveCookie(CookiesModel cookie) {
        File file = new File("D:\\Projects\\Document\\cookies.json");
        if (file.exists()) {
            String s = FileUtil.readString(file, CharsetUtil.UTF_8);
            List<CookiesModel> listArr = JSON.parseArray(s, CookiesModel.class);
            List<CookiesModel> collect = listArr.stream()
                    .filter(u -> u.getWebName().equalsIgnoreCase(cookie.getWebName())).collect(
                            Collectors.toList());
            if (CollUtil.isEmpty(collect)) {
                listArr.add(cookie);
            } else {
                collect.get(0).setCookies(cookie.getCookies());
            }
            FileUtil.writeString(JSON.toJSONString(listArr), file, CharsetUtil.UTF_8);
        } else {
            List<CookiesModel> listArr = new ArrayList<>();
            listArr.add(cookie);
            FileUtil.writeString(JSON.toJSONString(listArr), file, CharsetUtil.UTF_8);
        }
        return "succ";
    }
    public static String getCookie(String webName) {
        try {
            File file = new File("D:\\Projects\\Document\\cookies.json");
            if (file.exists()) {
                String s = FileUtil.readString(file, CharsetUtil.UTF_8);
                List<CookiesModel> listArr = JSON.parseArray(s, CookiesModel.class);
                List<CookiesModel> collect = listArr.stream()
                        .filter(u -> u.getWebName().equalsIgnoreCase(webName)).collect(
                                Collectors.toList());
                if (CollUtil.isEmpty(collect)) {
                    return "";
                } else {
                    return collect.get(0).getCookies();
                }
            } else {
                return "";
            }
        }catch (Exception ex){
            return "";
        }
    }
} 