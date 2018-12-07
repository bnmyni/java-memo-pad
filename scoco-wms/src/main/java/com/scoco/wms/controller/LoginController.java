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
 * 用户登录
 *
 * @author sunke
 * @date 2018/12/7
 */
@Controller
public class LoginController {

    private final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(LoginUserFo user, Model model) {

        LoginUserVo userVo = userService.login(user);
        model.addAttribute("user", userVo);
        model.addAttribute("msg", userVo.getMsg());
        model.addAttribute("content", " content/index::html");
        LOG.info("用户登录返回信息: {}", model);
        return StringUtils.isEmpty(userVo.getMsg()) ? "index" : "login";
    }

}