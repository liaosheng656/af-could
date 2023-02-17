package com.af.common.exception;
//import feign.FeignException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常捕获
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/04/01 17:36:56 
 *
 */
@Slf4j
//@ControllerAdvice({"com.github.wxiaoqi.security","com.gpdi.aep"})
@ControllerAdvice({"com.af"})
@ResponseBody
public class GlobalExceptionHandler {
	

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String httpRequestMethodHandler(HttpRequestMethodNotSupportedException ex) {
        return "请求方式有误";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        return "找不到的资源";
    }

    /**
     *处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常，详情继续往下看代码
     */
    @ExceptionHandler(BindException.class)
    public String BindExceptionHandler(BindException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return message;
    }

    /**
     *处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public String ConstraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        return message;
    }

    /**
     *处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return message;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public String httpMessageNotReadableExceptionHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    /**
     * feign异常
     *
     * @param f
     * @return
     */
//    @ExceptionHandler(FeignException.class)
//    public BaseResponse feignExceptionHandler(FeignException f) {
//        log.error("未经处理的异常", f);
//        return new BaseResponse(f.status(), f.getMessage());
//    }

    @ExceptionHandler(BaseException.class)
    public String baseExceptionHandler(HttpServletResponse response, BaseException ex) {
        log.error(ex.getMessage(),ex);
        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public String otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(500);
        log.error(ex.getMessage(),ex);
        return "系统内部错误，请联系管理员解决";
    }

}