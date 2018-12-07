package com.scoco.wms.configuration;

import com.scoco.wms.filter.LoginFilter;
import com.scoco.wms.filter.MenuFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统filter配置
 * @author sunke
 * @date 2018/12/7
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean menuFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        MenuFilter actionFilter = new MenuFilter();
        registrationBean.setFilter(actionFilter);
        List<String> urlPatterns = new ArrayList();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);
        registrationBean.setOrder(0);
        return registrationBean;
    }
//
//    @Bean
//    public FilterRegistrationBean loginFilterRegistrationBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        LoginFilter actionFilter = new LoginFilter();
//        registrationBean.setFilter(actionFilter);
//        List<String> urlPatterns = new ArrayList();
//        urlPatterns.add("/*");
//        registrationBean.setUrlPatterns(urlPatterns);
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }
}
