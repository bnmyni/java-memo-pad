package com.scoco.wms.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 项目名称: scoco-wms 包名称: com.scoco.wms.vo 类名称: LoginUserVo.java 类描述: 用户登录响应消息 创建人: sunke 版本号: 1.0.0.0
 * 创建时间: 2018/11/23 10:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginUserVo {

    private String userName;

    private String realName;

    private String mobile;

    private String msg;
}