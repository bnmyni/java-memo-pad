package com.scoco.wms.fo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 项目名称: 伟明丰查询系统
 * 包名称: com.scoco.wms.fo
 * 类名称: LoginUserFo.java
 * 类描述: 用户登录表单
 * 创建人: sunke
 * 版本号: 1.0.0.0
 * 创建时间: 2018/11/22 15:46
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserFo {
    private String userName;

    private String password;
}