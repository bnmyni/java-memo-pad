package com.scoco.wms.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.scoco.wms.dao.UserDao;
import com.scoco.wms.entity.User;
import com.scoco.wms.fo.LoginUserFo;
import com.scoco.wms.service.UserService;
import com.scoco.wms.vo.LoginUserVo;

/**
 * 用户业务处理
 *
 * @author sunke
 * @version 1.0.0.0
 * @date 2018/12/21
 */
@Service
public class UserServiceImpl implements UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;


    @Override
    public LoginUserVo login(LoginUserFo user) {
        LoginUserVo userVo = new LoginUserVo();
        try {
            if (user == null || StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getPassword())) {
                userVo.setMsg("请输入用户名或密码!");
                return userVo;
            }
            User u = userDao.login(user);
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