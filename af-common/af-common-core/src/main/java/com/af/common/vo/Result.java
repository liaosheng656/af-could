package com.af.common.vo;
import java.io.Serializable;

import com.af.common.constant.GlobalReturnCode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.annotation.Generated;

/**
 * 接口返回数据格式
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/03/31 16:31:04 
 *
 */
@ApiModel(value = "接口返回对象", description = "接口返回对象")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    @ApiModelProperty(value = "成功标志")
    private boolean success = true;

    /**
     * 返回处理消息
     */
    @ApiModelProperty(value = "返回处理消息")
    private String message = "操作成功！";

    /**
     * 返回代码
     */
    @ApiModelProperty(value = "返回代码")
    private Integer code = GlobalReturnCode.SC_OK_200;

    /**
     * 返回数据对象 data
     */
    @ApiModelProperty(value = "返回数据对象")
    private T result;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private long timestamp = System.currentTimeMillis();

    
    
	public boolean getSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public Integer getCode() {
		return code;
	}

	public T getResult() {
		return result;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Generated("SparkTools")
	private Result(Builder<T> builder) {
		this.success = builder.success;
		this.message = builder.message;
		this.code = builder.code;
		this.result = builder.result;
		this.timestamp = builder.timestamp;
	}

    public Result() {

    }

    public Result<T> success(String message) {
        this.message = message;
        this.code = GlobalReturnCode.SC_OK_200;
        this.success = true;
        return this;
    }

    public static <T> Result<T> ok() {
    	Result<T> r = new Result.Builder<T>()
    			  				.success(true)
    			  				.code(GlobalReturnCode.SC_OK_200)
    			  				.build();
        return r;
    }
    
    public static <T> Result<T> ok(T t) {
        
    	Result<T> r = new Result.Builder<T>()
				  				.success(true)
				  				.code(GlobalReturnCode.SC_OK_200)
				  				.result(t)
				  				.build();
        
        return r;
    }

    public static <T> Result<T> ok(T t,String message) {
    	Result<T> r = new Result.Builder<T>()
				  				.success(true)
				  				.code(GlobalReturnCode.SC_OK_200)
				  				.result(t)
				  				.message(message)
				  				.build();

    	return r;
    }


    public static <T> Result<T> okk(T t) {
    	
    	Result<T> r = new Result.Builder<T>()
				  				.success(true)
				  				.code(GlobalReturnCode.SC_OK_200)
				  				.result(t)
				  				.build();

    	return r;
    }

    public static <T> Result<T> error(String msg) {
        return error(GlobalReturnCode.SYS_ERROR, msg);
    }

/*    public static <T> Result<T> error(String msg, T data) {
        return error(GlobalReturnCode.SYS_ERROR, msg, data);
    }*/

    public static <T> Result<T> error(int code, String message) {
    	Result<T> r = new Result.Builder<T>()
				  				.success(false)
				  				.code(GlobalReturnCode.SYS_ERROR)
				  				.message(message)
				  				.build();

    	return r;
    }


    public Result<T> error500(String message) {
    	
    	Result<T> r = new Result.Builder<T>()
  				.success(false)
  				.code(GlobalReturnCode.SYS_ERROR)
  				.message(message)
  				.build();

        return r;
    }

    /**
     * 无权限访问返回结果
     */
    public static Result<Object> noauth(String msg) {
        return error(GlobalReturnCode.SC_JEECG_NO_AUTHZ, msg);
    }

	@Generated("SparkTools")
	public static <T> Builder <T> builder() {
		Builder<T> builder = new Builder<>();
		
		return builder;
	}

	/**
	 * 构造器
	 * @Description: 
	 * @author: liaohuiquan  
	 * @date: 2022/04/14 11:08:57 
	 *
	 */
	@Generated("SparkTools")
	public static final class Builder<T> {
		private boolean success = true;
		private String message = "操作成功！";
		private Integer code = GlobalReturnCode.SC_OK_200;
		private T result;
		private long timestamp = System.currentTimeMillis();

		public Builder() {
		}

		public Builder<T> success(final boolean success) {
			this.success = success;
			return this;
		}

		public Builder<T> message(String message) {
			this.message = message;
			return this;
		}

		public Builder<T> code(Integer code) {
			this.code = code;
			return this;
		}

		public Builder<T> result(T result) {
			this.result = result;
			return this;
		}

		public Builder<T> timestamp(long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Result<T> build() {
			return new Result<T>(this);
		}
	}
    
}