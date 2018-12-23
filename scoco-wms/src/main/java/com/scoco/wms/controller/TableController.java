package com.scoco.wms.controller;

import com.scoco.wms.fo.AddTableFo;
import com.scoco.wms.fo.LoginUserFo;
import com.scoco.wms.vo.LoginUserVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 数据表创建控制层
 *
 * @author sunke
 * @version 1.0.0.0
 * @date 2018/12/19
 */
@Controller
public class TableController {

    private final static Logger LOG = LoggerFactory.getLogger(TableController.class);

    @RequestMapping(value = "/db/add/table", method = RequestMethod.POST)
    public String login(AddTableFo fo, Model model) {

        LOG.info("请求对象：{}", fo);
        LoginUserVo userVo = new LoginUserVo();
        userVo.setRealName("孙科");
        model.addAttribute("user", userVo);
        LOG.info("用户登录返回信息: {}", model);
        return "form-default";
    }
}
