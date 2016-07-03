package org.jfw.apt.web.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface RequestHeader {
	String value() default "";
//	Class<?> clazz() default Object.class;
	String defaultValue() default "";
//	FieldParam[] fields() default {};
//	String[] excludeFields() default {};
}
