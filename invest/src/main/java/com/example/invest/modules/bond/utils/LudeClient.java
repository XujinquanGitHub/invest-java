package com.example.invest.modules.bond.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * @program: invest
 * @description: 债券数据加载客户端
 * @author: 许金泉
 * @create: 2023-05-19 18:39
 **/
public class LudeClient {

    public static JSONArray loadData(){
        String s = FileUtil.readString("D:\\Projects\\data\\lude.json", "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(s, JSONObject.class);
        return jsonObject.getJSONArray("rowData");
    }

    public static JSONArray loadDataXiaLocal(){
        String s = FileUtil.readString("D:\\Projects\\data\\lude-xia.json", "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(s, JSONObject.class);
        return jsonObject.getJSONArray("rowData");
    }
} 