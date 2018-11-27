package com.scoco.wms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 项目名称: 伟明丰查询系统
 * 包名称: com.scoco.wms.controller
 * 类名称: IndexController.java.java
 * 类描述: 首页控制
 * 创建人: sunke
 * 版本号: 1.0.0.0
 * 创建时间: 2018/11/22 11:31
 */
@Controller
public class IndexController {
    @RequestMapping("/index")
    String index() {
        return "login";
    }
}
