package com.scoco.wms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scoco.wms.fo.LoginUserFo;
import com.scoco.wms.service.UserService;
import com.scoco.wms.vo.LoginUserVo;

/**
 * 项目名称:  伟明丰查询系统 包名称: com.scoco.wms.controller 类名称: LoginController.java 类描述: 系统登录功能控制 创建人: sunke
 * 版本号: 1.0.0.0 创建时间: 2018/11/22 14:10
 */
@Controller
//@RequestMapping("/login")
public class LoginController {

    private final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String toLoginPage() {
        return "login";
    }

    @RequestMapping(value = "/action", method = RequestMethod.POST)
    public String login(LoginUserFo user, Model model) {

        LoginUserVo userVo = new LoginUserVo();
        userVo.setRealName("孙科");
        // userService.login(user);
        model.addAttribute("user", userVo);
        model.addAttribute("msg", userVo.getMsg());
        LOG.info("用户登录返回信息: {}", model);
        return StringUtils.isEmpty(userVo.getMsg()) ? "index" : "login";
    }

}