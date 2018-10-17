package com.aspire.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aspire.cache.DemoService;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.controller
 * fileName: CacheController.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/15 18:18
 */
@Controller
public class CacheController {
    @Autowired
    DemoService demoService;

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    @ResponseBody
    public String getId(String id) {
        return demoService.getId(id);
    }

    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String getTestId(String id) {
        return demoService.getId(id);
    }

}