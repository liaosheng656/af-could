package com.af.common.exception;

/**
 * 基础异常
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/04/01 17:36:30 
 *
 */
public class BaseException extends RuntimeException{

	private static final long serialVersionUID = -3149896775248138814L;

	public BaseException(String message) {
		super(message);
	}
}
