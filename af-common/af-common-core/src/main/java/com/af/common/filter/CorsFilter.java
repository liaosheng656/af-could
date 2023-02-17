package com.af.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.af.common.constant.GlobalReturnCode;
import com.af.common.vo.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义(cors跨域处理)过滤器
 * @author: liaohuiquan  
 * @date: 2021/08/24 14:39:46 
 *
 */
@Slf4j
@Component
public class CorsFilter implements Filter {

    /**
     * 浏览器向IDaas平台发出请求地址
     */
//    @Value("${idaas.login-url}")
//    private String idaasLoginUrl;

    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {

        //logger.info("初始化过滤器corsFilter：", filterConfig.getFilterName());
    }

    /**
     * 设置请求头，处理跨域问题
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        
        //logger.info("开始进入过滤器corsFilter：");
        HttpServletResponse response = (HttpServletResponse) servletResponse;  
        response.setHeader("Access-Control-Allow-Origin", "*");  
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");  
        response.setHeader("Access-Control-Max-Age", "3600");  
        response.setHeader("Access-Control-Allow-Headers", "*"); 
        //访问控制允许凭据，true为允许
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 浏览器是会先发一次options请求，如果请求通过，则继续发送正式的post请求
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        
        String options = "OPTIONS";
        // 配置options的请求返回
        if (options.equals(request.getMethod())) {
            response.setStatus(200);
            response.getWriter().write("OPTIONS returns OK");
            return;
        }
        
        //获取请求路径
        String requestUri = request.getRequestURI();
        //logger.warn("请求的接口为：" + requestUri);
        
        try {
            //放行
            filterChain.doFilter(servletRequest, servletResponse);
		} catch (Exception e) {
			
			//这里的异常一般为token处理时，发生的异常，其他异常默认为系统异常
			logger.error("请求经过过滤器时，发生异常：{}",e);
			
			String message = "系统内部异常，请稍后再试！";
			
			if(e.getCause() != null && e.getCause().getMessage() != null) {
				message = e.getCause().getMessage();
			}
			
	        Result result  = Result.builder().message(message)
	        						 .code(GlobalReturnCode.SYS_ERROR)
	        						 .success(false)
	        						 .build();
	        
//			if (StrUtil.isNotEmpty((String) e.getMessage()) && ((String) e.getMessage()).indexOf(AuthenticationException.class.getName()) != -1) {
//	        	//token不正确或过期标识，800
//	        	result.setCode(TOKEN_ERROR);
//			}
			
			request.setAttribute("filter.error.result", result);
			
			//log.error("异常分发成功！");
            //将异常分发到/error/exthrow控制器
            request.getRequestDispatcher("/error/exthrow").forward(request, response);
            
		}
    }

    @Override
    public void destroy() {

        //logger.info("销毁过滤器corsFilter");
    }
}