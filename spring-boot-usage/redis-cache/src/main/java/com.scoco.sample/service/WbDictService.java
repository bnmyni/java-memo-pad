package com.scoco.sample.service;

import java.util.List;
import com.scoco.sample.entity.WbDict;

/**
 * 数据字典service
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.sample.cache.service
 * fileName: WbDictService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/11/08 16:51
 */
public interface WbDictService {


    List<WbDict> list(String type);

    void remove(String type);
}