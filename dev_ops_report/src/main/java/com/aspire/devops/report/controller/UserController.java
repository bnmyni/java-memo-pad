package com.aspire.devops.report.controller;

import com.aspire.devops.report.dto.LoginRequest;
import com.aspire.devops.report.dto.LoginResponse;
import com.aspire.devops.report.model.UserInfo;
import com.aspire.devops.report.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 系统用户管理
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.controller
 * fileName: UserController.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/08/08 10:15
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private LoginResponse login(@RequestBody LoginRequest request, HttpSession session) {
        LoginResponse response = new LoginResponse();
        logger.info("/user/login request params:{}", request);
        UserInfo info = userService.login(request.getUsername(), request.getPassword());

        if (info != null) {
            String uuid = UUID.randomUUID().toString();
            response.setLoin(true);
            response.setToken(uuid);
            session.setAttribute("token", uuid);
            response.setUsername(info.getUsername());
        } else {
            response.setLoin(false);
        }
        return response;

    }
}