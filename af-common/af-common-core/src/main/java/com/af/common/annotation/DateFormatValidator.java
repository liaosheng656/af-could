package com.af.common.annotation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 *
 * @apiNote 自定义注解逻辑实现
 * @version 3.0.0
 *
 */
@Slf4j
public class DateFormatValidator implements ConstraintValidator<DateFormat, Object> {
    
	protected String format;
	
	private boolean must;
	
	/**
	 * 初始化
	 */
	@Override
    public void initialize(DateFormat dateFormat) {
		this.format = dateFormat.format();
		this.must = dateFormat.must();
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
			try {
					
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				//严格解析日期，如果日期不合格就抛异常，不会自动计算
				dateFormat.setLenient(false);
				dateFormat.parse(obj.toString());
			} catch (Exception e) {
				log.error("发生字符串转时间异常");
				return false;
			}
		}

		//非必传时，值不为空，也要进行校验
		if(obj != null) {
			try {
				
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				//严格解析日期，如果日期不合格就抛异常，不会自动计算
				dateFormat.setLenient(false);
				dateFormat.parse(obj.toString());
			} catch (Exception e) {
				log.error("发生字符串转时间异常");
				return false;
			}
		}
        return true;
	}

}