package com.example.invest.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CookiesModel {
    private String webName;
    private String cookies;
} 