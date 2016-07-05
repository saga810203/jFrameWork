package org.jfw.apt.web.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface FieldParam {
	String value();
	String valueClassName() default "";
	String paramName() default "";
	String defaultValue() default "";
}
