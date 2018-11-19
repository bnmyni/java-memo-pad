package com.scoco.sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.scoco.sample.entity.WbDict;
import com.scoco.sample.fo.WbDictListFO;
import com.scoco.sample.service.WbDictService;

/**
 * 数据字典控制层
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.sample.cache.controller
 * fileName: WbDictController.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/11/08 17:02
 */
@RestController
@RequestMapping("/v1/cache/dict")
public class WbDictController {

    @Autowired
    private WbDictService wbDictService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public List<WbDict> list(@RequestBody WbDictListFO wbDict) {

        return wbDictService.list(wbDict.getDictType());
    }

    @RequestMapping(value = "/remove/{type}", method = RequestMethod.DELETE)
    public void remove(@PathVariable(value = "type") String type) {

        wbDictService.remove(type);
    }
}