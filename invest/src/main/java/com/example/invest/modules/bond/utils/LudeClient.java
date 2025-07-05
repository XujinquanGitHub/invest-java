package com.example.invest.modules.bond.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.parser.Feature;
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
//        JSONObject jsonObject = JSONObject.parseObject(s, JSONObject.class);
        // 使用 Feature.AllowArbitraryCommas 等特性忽略部分格式错误
        JSONObject jsonObject = JSON.parseObject(s, Feature.AllowUnQuotedFieldNames, Feature.IgnoreNotMatch);
        Object o = jsonObject.getJSONObject("response").getJSONObject("_pages_content")
                .getJSONObject("children").getJSONObject("props").getJSONArray("children").get(3);
        JSONArray rowData = ((JSONObject)o)
                .getJSONObject("props").getJSONObject("children")
                .getJSONObject("props").getJSONObject("children")
                .getJSONObject("props")
                .getJSONArray("rowData");
        return rowData;
    }

    public static JSONArray loadDataXiaLocal(){
        String s = FileUtil.readString("D:\\Projects\\data\\lude-xia.json", "UTF-8");
        JSONObject jsonObject = JSON.parseObject(s, Feature.AllowUnQuotedFieldNames, Feature.IgnoreNotMatch);
        Object o = jsonObject.getJSONObject("response").getJSONObject("_pages_content")
                .getJSONObject("children").getJSONObject("props").getJSONArray("children").get(1);
        JSONArray rowData = ((JSONObject)o)
                .getJSONObject("props").getJSONObject("children")
                .getJSONObject("props").getJSONObject("children")
                .getJSONObject("props")
                .getJSONArray("rowData");
        return rowData;
    }
} 