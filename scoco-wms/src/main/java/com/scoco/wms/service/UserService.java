package com.scoco.wms.service;

import com.scoco.wms.fo.LoginUserFo;
import com.scoco.wms.vo.LoginUserVo;

/**
 * 项目名称: 伟明丰查询系统
 * 包名称: com.scoco.wms.service
 * 类名称: UserService.java
 * 类描述: 系统用户Service
 * 创建人: sunke
 * 版本号: 1.0.0.0
 * 创建时间: 2018/11/22 20:44
 */
public interface UserService {
    LoginUserVo login(LoginUserFo user);
}