package com.scoco.wms.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.scoco.wms.entity.User;
import com.scoco.wms.fo.LoginUserFo;
import com.scoco.wms.repository.UserRepository;
import com.scoco.wms.service.UserService;
import com.scoco.wms.vo.LoginUserVo;

/**
 * 项目名称: 伟明丰查询系统
 * 包名称: com.scoco.wms.service.impl
 * 类名称: UserServiceImpl.java
 * 类描述: 系统用户Service实现
 * 创建人: sunke
 * 版本号: 1.0.0.0
 * 创建时间: 2018/11/22 20:46
 */
@Service
public class UserServiceImpl implements UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;


    @Override
    public LoginUserVo login(LoginUserFo user) {
        LoginUserVo userVo = new LoginUserVo();
        try {
            if (user == null || StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getPassword())) {
                userVo.setMsg("请输入用户名或密码!");
                return userVo;
            }
            User u = userRepository.login(user.getUserName(), user.getPassword());
            if (u == null) {
                userVo.setMsg("用户名或密码不存在!");
                return userVo;
            }
            BeanUtils.copyProperties(u, userVo);
            return userVo;
        } catch (Exception e) {
            LOG.error("用户登录异常", e);
            userVo.setMsg("用户登录异常!");
        }
        return userVo;
    }
}