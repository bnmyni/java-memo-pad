package com.scoco.sample.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import com.scoco.sample.dao.WbDictDao;
import com.scoco.sample.entity.WbDict;
import com.scoco.sample.service.WbDictService;

/**
 * 数据字典service实现
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.sample.cache.service.impl
 * fileName: WbDictServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/11/08 16:53
 */
@Service
public class WbDictServiceImpl implements WbDictService {

    @Autowired
    private WbDictDao wbDictDao;

    // 这里是为了测试不同cache的过期时间，实际使用多value将导致问题排查非常困难，请慎重使用
    @Cacheable(value = {"dict", "user"}, key = "#type")
    @Override
    public List<WbDict> list(String type) {
        return wbDictDao.listByType(type);
    }

    @CacheEvict(value="dict", key = "#type")
    @Override
    public void remove(String type) {
        System.out.println("remove cache:" + type);
    }
}