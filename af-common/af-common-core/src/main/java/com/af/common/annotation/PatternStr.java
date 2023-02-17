package com.af.common.annotation;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 自定义正则表达式校验
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/03/24 
 *
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {PatternStrValidator.class})
public @interface PatternStr {

	/**
	 * 正则表达式
	 * @return
	 */
    String regexp() default "";
    
    String message() default "格式错误";
    
    /**
     * 是否必传，true必传，false非必传
     * @return
     */
    boolean must() default false;
    
    Class<?>[] groups() default { };
    
    Class<? extends Payload>[] payload() default { };


}