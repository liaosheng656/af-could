package com.af.common.annotation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 *
 * @apiNote 自定义注解逻辑实现
 * @version 3.0.0
 *
 */
@Slf4j
public class PatternStrValidator implements ConstraintValidator<PatternStr, Object> {
    
	protected String regexp;
	
	private boolean must;
	
	/**
	 * 初始化
	 */
	@Override
    public void initialize(PatternStr patternStr) {
		this.regexp = patternStr.regexp();
		this.must = patternStr.must();
		log.info("正则表格式："+regexp);
    }

	/**
	 * 校验
	 */
	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		
		if(this.must) {
			
			if(obj == null) {
				return false;
			}
			
			String str = obj.toString();
			
	        Pattern pattern = Pattern.compile(this.regexp);
	        if (pattern.matcher(str).matches() == false) {
	            return false;
	        }
		}

		//非必传时，值不为空，也要进行校验
		if(obj != null) {
			String str = obj.toString();
			
	        Pattern pattern = Pattern.compile(this.regexp);
	        if (pattern.matcher(str).matches() == false) {
	            return false;
	        }
		}
        return true;
	}

}