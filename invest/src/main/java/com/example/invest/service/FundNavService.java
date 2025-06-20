package com.example.invest.service;

import com.example.invest.model.FundNavModel;
import java.util.List;
import java.util.Map;

public interface FundNavService {
    Map<String, List<FundNavModel>> loadAllNav();
    List<FundNavModel> loadFundNav(String fundId);
    List<FundNavModel> loadTwentyDayFundNavForSiNa(String fundId);
    // 可根据需要添加更多方法
} 