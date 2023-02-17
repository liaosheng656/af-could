package com.af.common.constant;
/**
 * 全局返回状态码
 */
public interface GlobalReturnCode {


    //全局成功码
    Integer SC_OK_200 = 200;

    //系统错误
    Integer SYS_ERROR = 500;

    Integer SC_JEECG_NO_AUTHZ = 510;//访问权限认证未通过

    Integer SC_OK_404 = 404;//404

    Integer SC_OK_405 = 405;//请求类型错误


    Integer SERVICE_ERROR_CODE = 600;//业务类型异常

    Integer PARAM_EXCEPTION_CODE = 700;//参数类型错误

    Integer TOKEN_ERROR = 800;//token类型错误

    Integer auth_error = 900;//操作权限异常
}
