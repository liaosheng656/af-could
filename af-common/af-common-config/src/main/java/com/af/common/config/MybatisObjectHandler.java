package com.af.common.config;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * 
 * @author: liaohuiquan  
 * @date: 2021/07/07 23:42:27 
 *
 */
@Component
public class MybatisObjectHandler implements MetaObjectHandler {
	

	@Override
	public void insertFill(MetaObject metaObject) {
	    setFieldValByName("createTime", new Date(),metaObject);
	    setFieldValByName("updateTime",new Date(),metaObject);
	}
	
	@Override
	public void updateFill(MetaObject metaObject) {
	    setFieldValByName("updateTime",new Date(),metaObject);
	}

}