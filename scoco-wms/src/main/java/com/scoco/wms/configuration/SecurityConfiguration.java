package com.scoco.wms.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 项目名称: 伟明丰查询系统 包名称: com.scoco.wms 类名称: SecurityConfiguration.java.java 类描述: 系统web安全鉴权 创建人: sunke
 * 版本号: 1.0.0.0 创建时间: 2018/11/22 11:29
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeRequests().antMatchers( "/css/**", "/fonts/**", "/img/**", "/js/**",
//                "/plugins/**")
//                .permitAll()
//                .antMatchers("/login/action", "/login/page", "/login").permitAll()
//                .anyRequest().authenticated().and().formLogin().loginPage("/login/page")
//                .defaultSuccessUrl("/index").failureForwardUrl("/login/page").permitAll()
//                // 开启cookie，设置有效期和私钥
//                .and().rememberMe().tokenValiditySeconds(60*60*24*7).key("scoco.com")
//                // 设置注销及注销后的跳转页面
//                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll();

        // csrf安全拦截
        httpSecurity.csrf().disable();
        httpSecurity.requestCache().disable();
        httpSecurity.headers().frameOptions().disable();
    }

}