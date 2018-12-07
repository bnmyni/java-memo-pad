package com.scoco.wms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 菜单路由控制
 *
 * @author sunke
 * @date 2018/12/7
 */
@Controller
public class MenuRomteController {

    private final static Logger LOGGER = LoggerFactory.getLogger(MenuRomteController.class);

    @RequestMapping("/menu/{first}/{second}")
    String remote(@PathVariable String first, @PathVariable String second, Model model) {
        String common = "common";
        if (StringUtils.isEmpty(first) || StringUtils.isEmpty(second)) {
            LOGGER.info("unable remote to page {}-{}", first, second);
            return "404";
        }

        if (common.equals(first)) {
            return second;
        }
        return first + "-" + second;
    }

}
