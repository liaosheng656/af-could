package com.af.common.filter;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 装配自定义过滤器Filter（使其产生作用）
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/04/01 17:26:45 
 *
 */
@Configuration
public class FilterConfig {

    @Autowired
    private CorsFilter corsFilter;

	/**
     * 装配（注册）自定义的过滤器，并加入Spring中
     * @return
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> twoFilter() {
    	//创建过滤器的注册器
        FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>();

        //装配自定义的过滤器
        filterRegistrationBean.setFilter(corsFilter);

        //当有多个自定义过滤器的时候，可以通过设置order来控制过滤器执行顺序
        //order的值越大，优先级越高
        filterRegistrationBean.setOrder(2);

        //设置请求（拦截）地址
        filterRegistrationBean.setUrlPatterns(new ArrayList<>(Arrays.asList("*")));

        return filterRegistrationBean;
    }

}