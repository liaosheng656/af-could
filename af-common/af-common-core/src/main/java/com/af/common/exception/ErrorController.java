package com.af.common.exception;

import com.af.common.vo.Result;

/**
 * 过滤器异常处理
 */

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Slf4j
@RestController
public class ErrorController extends BasicErrorController {


    public ErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    /**
     * 错误处理
     */
    @Override
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        //Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        Map<String, Object> body = getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION, ErrorAttributeOptions.Include.MESSAGE));
        HttpStatus status = getStatus(request);
        //自定义的错误信息类
//        status.value();//错误代码，
//        body.get("message").toString();//错误信息

        Result result  = Result.builder().message(body.get("message").toString())
        						 .code(status.value())
        						 .success(false)
        						 .build();
        							

//        //判断是否为shiro验证异常（token不正确或过期）
//        if (StrUtil.isNotEmpty((String) body.get("message")) && ((String) body.get("message")).indexOf(AuthenticationException.class.getName()) != -1) {
//            //token不正确或过期标识
//            result.setCode(TOKEN_ERROR);
//            String msg = result.getMessage();
//            result.setMessage(msg.substring(msg.lastIndexOf(":") + 1, msg.length()));
//        }
        
        String jsonString = JSONObject.toJSONString(result);
        
        Map<String, Object> object = JSONObject.parseObject(jsonString,Map.class);
        
        return ResponseEntity.ok(object);
    }

    /**
     * 处理过滤器转发过来的异常
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/exthrow")
    public Object notLogin(HttpServletRequest request) {

        // 取出错误信息
        Object attribute = request.getAttribute("filter.error.result");

        //log.error("收到过滤器转发过来的异常消息");

        return attribute;
    }


    private static final String ERROR_PATH = "/error";

    /**
     * web页面错误处理
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    @ResponseBody
    public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
        return JSONObject.toJSONString(getResult(request));
    }

    /**
     * 除web页面外的错误处理，比如json/xml等
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public Result<String> errorApiHander(HttpServletRequest request) {
        return getResult(request);
    }

    private Result<String> getResult(HttpServletRequest request) {
        //ServletWebRequest requestAttributes = new ServletWebRequest(request);
        //Map<String, Object> attr =this.errorAttributes.getErrorAttributes((WebRequest) request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION, ErrorAttributeOptions.Include.MESSAGE));
        Map<String, Object> attr = getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION, ErrorAttributeOptions.Include.MESSAGE));

        Result<String> res = Result.error((int) attr.get("status"), (String) attr.get("message"));
        
        return res;
    }
}
